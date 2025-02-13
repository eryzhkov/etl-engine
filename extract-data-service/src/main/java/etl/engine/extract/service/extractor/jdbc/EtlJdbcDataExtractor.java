package etl.engine.extract.service.extractor.jdbc;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.extract.exception.EtlExtractDataException;
import etl.engine.extract.model.EtlStreamData;
import etl.engine.extract.service.extractor.EtlDataExtractor;
import etl.engine.extract.service.extractor.jdbc.model.DataStreamConfig;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

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

            // The stub ETL-configuration should contain a valid database credentials!
            log.info("Initialise and execute the query...");

        } catch (Exception e) {
            throw new EtlExtractDataException(e);
        }

        // Just a stub implementation
        return new EtlStreamData(
                new ByteArrayInputStream(new byte[0]),
                dataStreamConfig.getName(),
                0,
                0);
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
