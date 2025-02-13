package etl.engine.extract.service.extractor.jdbc;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import etl.engine.extract.exception.EtlExtractDataException;
import etl.engine.extract.model.EtlStreamData;
import etl.engine.extract.service.extractor.EtlDataExtractor;
import etl.engine.extract.service.extractor.jdbc.model.DataStreamConfig;
import etl.engine.extract.service.extractor.jdbc.model.MappingRule;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class implements an extraction logic for the JDBC-datasource.
 */
@ToString
@Slf4j
public class EtlJdbcDataExtractor implements EtlDataExtractor, AutoCloseable {

    private final static String DB_TYPE_PTR = "/config/dbType";
    private final static String HOST_PTR = "/config/host";
    private final static String PORT_PTR = "/config/port";
    private final static String DATABASE_PTR = "/config/database";
    private final static String USER_PTR = "/config/user";
    private final static String PASSWORD_PTR = "/config/password";
    @Deprecated
    private final static String DATA_STREAM_NAME_PTR = "/name";

    private final String databaseType;
    private final String host;
    private final int port;
    private final String databaseName;
    private final String userName;
    private final String userPassword;

    private final ObjectMapper mapper;
    private final EtlJdbcDataSource dataSource;

    /**
     * Reads the configuration parameters.
     * @param extractorConfig the 'extractor' section of the ETL-configuration for the 'data-extract' phase.
     */
    public EtlJdbcDataExtractor(JsonNode extractorConfig) {
        this.databaseType = readValueAsString(extractorConfig, DB_TYPE_PTR);
        this.host = readValueAsString(extractorConfig, HOST_PTR);
        this.port = readValueAsInt(extractorConfig, PORT_PTR);
        this.databaseName = readValueAsString(extractorConfig, DATABASE_PTR);
        this.userName = readValueAsString(extractorConfig, USER_PTR);
        this.userPassword = readValueAsString(extractorConfig, PASSWORD_PTR);
        this.dataSource = new EtlJdbcDataSource(
                databaseType,
                host,
                port,
                databaseName,
                userName,
                userPassword);
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Override
    public EtlStreamData extractData(JsonNode dataStreamDefinition) throws EtlExtractDataException {

        DataStreamConfig dataStreamConfig;
        try {
            dataStreamConfig = mapper.treeToValue(dataStreamDefinition, DataStreamConfig.class);
        } catch (JsonProcessingException e) {
            throw new EtlExtractDataException(e);
        }
        log.debug("Parsed data stream config: {}", dataStreamConfig);

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement(dataStreamConfig.getQuery().getSql());
            ) {

            long totalIn = 0;
            long totalFailed = 0;

            Map<String, MappingRule> mappingRuleMap = dataStreamConfig.getMappings();

            ResultSet rs = query.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int totalColumns = resultSetMetaData.getColumnCount();
            List<JsonNode> extractedObjects = new ArrayList<>();

            log.debug("Iterates over the result set to extract data.");
            while (rs.next()) {

                ObjectNode extractedObject = mapper.createObjectNode();

                boolean isRowOk = false;
                for(int index = 1; index <= totalColumns; index++) {
                    String columnName = resultSetMetaData.getColumnName(index);
                    MappingRule mappingRule = mappingRuleMap.get(columnName);
                    if (mappingRule != null) {
                        JDBCType returnedJdbcType = JDBCType.valueOf(resultSetMetaData.getColumnType(index));
                        String definedColumnType = mappingRule.getType();

                        if (returnedJdbcType.getName().equalsIgnoreCase(definedColumnType)) {
                            if (MappingRule.INTEGER_TYPE.equalsIgnoreCase(mappingRule.getType())) {
                                extractedObject.put(columnName, rs.getInt(columnName));
                                isRowOk = true;
                            } else if (MappingRule.STRING_TYPE.equalsIgnoreCase(mappingRule.getType())) {
                                extractedObject.put(columnName, rs.getString(columnName));
                                isRowOk = true;
                            } else {
                                //TODO Should the unsupported data type be reported somehow?
                                log.warn("Unsupported data type found - '{}'. The row is skipped.", mappingRule.getType());
                                isRowOk = false;
                            }
                        } else {
                            //TODO The whole row should be skipped!
                            log.warn("The column '{}' has unexpected data type: the expected '{}' but found '{}'. The row is skipped.",
                                    columnName, definedColumnType, returnedJdbcType);
                            isRowOk = false;
                        }

                    } else {
                        log.warn("The column '{}' was not found in the data stream configuration and is skipped.", columnName);
                    }
                }
                if (isRowOk) {
                    extractedObjects.add(extractedObject);
                    totalIn++;
                } else {
                    totalFailed++;
                }
            }
            log.debug("Extracted: {}", extractedObjects);
            log.debug("totalIn = {}, totalFailed = {}", totalIn, totalFailed);

            ArrayNode arrayNode = mapper.createArrayNode();
            arrayNode.addAll(extractedObjects);

            return new EtlStreamData(
                    new ByteArrayInputStream(mapper.writeValueAsBytes(arrayNode)),
                    dataStreamConfig.getName(),
                    totalIn,
                    totalFailed);

        } catch (Exception e) {
            throw new EtlExtractDataException(e);
        }
    }

    private String readValueAsString(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asText();
    }

    private int readValueAsInt(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asInt();
    }

    @Override
    public void close() throws Exception {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }
}
