package etl.engine.extract.service.extractor.jdbc;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import etl.engine.extract.exception.EtlExtractDataException;
import etl.engine.extract.model.EtlStreamData;
import etl.engine.extract.service.extractor.EtlDataExtractor;
import lombok.ToString;

import java.io.ByteArrayInputStream;

@ToString
public class EtlJdbcDataExtractor implements EtlDataExtractor {

    private final static String DB_TYPE_PTR = "/config/dbType";
    private final static String HOST_PTR = "/config/host";
    private final static String PORT_PTR = "/config/port";
    private final static String DATABASE_PTR = "/config/database";
    private final static String USER_PTR = "/config/user";
    private final static String PASSWORD_PTR = "/config/password";
    private final static String DATA_STREAM_NAME_PTR = "/name";

    private final String databaseType;
    private final String host;
    private final int port;
    private final String databaseName;
    private final String userName;
    private final String userPassword;

    public EtlJdbcDataExtractor(JsonNode extractorConfig) {
        this.databaseType = readValueAsString(extractorConfig, DB_TYPE_PTR);
        this.host = readValueAsString(extractorConfig, HOST_PTR);
        this.port = readValueAsInt(extractorConfig, PORT_PTR);
        this.databaseName = readValueAsString(extractorConfig, DATABASE_PTR);
        this.userName = readValueAsString(extractorConfig, USER_PTR);
        this.userPassword = readValueAsString(extractorConfig, PASSWORD_PTR);
    }

    @Override
    public EtlStreamData extractData(JsonNode dataStreamConfig) throws EtlExtractDataException {
        // Just a stub implementation
        return new EtlStreamData(
                new ByteArrayInputStream(new byte[0]),
                readValueAsString(dataStreamConfig, DATA_STREAM_NAME_PTR),
                0,
                0);
    }

    private String readValueAsString(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asText();
    }

    private int readValueAsInt(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asInt();
    }
}
