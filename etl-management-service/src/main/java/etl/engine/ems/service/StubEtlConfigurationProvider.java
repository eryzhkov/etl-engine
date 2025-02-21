package etl.engine.ems.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * The temporary ETL-configuration provided.
 * Will be deleted in the future.
 */
public class StubEtlConfigurationProvider {

    private final static String ETL_CONFIG_PATH = "/etl-configs/etl-configuration-test.json";

    public static JsonNode getStubEtlConfiguration() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        InputStream is = StubEtlConfigurationProvider.class.getResourceAsStream(ETL_CONFIG_PATH);
        return mapper.readTree(is);
    }

}
