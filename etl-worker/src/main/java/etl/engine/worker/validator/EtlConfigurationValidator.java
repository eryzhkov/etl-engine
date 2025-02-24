package etl.engine.worker.validator;

import com.fasterxml.jackson.databind.JsonNode;
import etl.engine.worker.exception.InvalidEtlConfigurationException;
import etl.engine.worker.util.JsonUtils;

/**
 * The class implements a very basic validator of an ETL-configuration.
 */
public class EtlConfigurationValidator {

    public static void validate(JsonNode configuration) throws InvalidEtlConfigurationException {
        // Validate the 'external-datasource' node is present and not empty.
        final String EXTERNAL_DATASOURCE_PATH = "/external-datasource";
        final String EXTERNAL_DATASOURCE_TYPE_PATH = "/external-datasource/type";
        if (JsonUtils.isNodeMissing(configuration, EXTERNAL_DATASOURCE_PATH)) {
            throw InvalidEtlConfigurationException.forMissingNode(EXTERNAL_DATASOURCE_PATH);
        }
        if (JsonUtils.isNodeEmpty(configuration, EXTERNAL_DATASOURCE_PATH)) {
            throw InvalidEtlConfigurationException.forEmptyNode(EXTERNAL_DATASOURCE_PATH);
        }
        if (JsonUtils.isNodeMissing(configuration, EXTERNAL_DATASOURCE_TYPE_PATH)) {
            throw InvalidEtlConfigurationException.forMissingNode(EXTERNAL_DATASOURCE_TYPE_PATH);
        }

        // Validate the 'internal-datasource' node is present and not empty.
        final String INTERNAL_DATASOURCE_PATH = "/internal-datasource";
        if (JsonUtils.isNodeMissing(configuration, INTERNAL_DATASOURCE_PATH)) {
            throw InvalidEtlConfigurationException.forMissingNode(EXTERNAL_DATASOURCE_PATH);
        }
        if (JsonUtils.isNodeEmpty(configuration, INTERNAL_DATASOURCE_PATH)) {
            throw InvalidEtlConfigurationException.forEmptyNode(EXTERNAL_DATASOURCE_PATH);
        }

        // Validate the 'streams' node is present and not empty.
        final String STREAMS_PATH = "/streams";
        if (JsonUtils.isNodeMissing(configuration, STREAMS_PATH)) {
            throw InvalidEtlConfigurationException.forMissingNode(EXTERNAL_DATASOURCE_PATH);
        }
        if (JsonUtils.isNodeEmpty(configuration, STREAMS_PATH)) {
            throw InvalidEtlConfigurationException.forEmptyNode(EXTERNAL_DATASOURCE_PATH);
        }
    }

}
