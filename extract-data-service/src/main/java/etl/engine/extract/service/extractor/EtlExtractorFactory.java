package etl.engine.extract.service.extractor;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import etl.engine.extract.exception.EtlUnknownExtractorTypeException;
import etl.engine.extract.service.extractor.jdbc.EtlJdbcDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EtlExtractorFactory {

    private static final String JDBC_EXTRACTOR = "jdbc";

    public EtlDataExtractor createExtractor(JsonNode extractorConfig) throws EtlUnknownExtractorTypeException {
        log.debug("Request for the extractor with configuration: {}.", extractorConfig);
        // Identify the extractor type.
        String extractorTypeValue = extractorConfig.at(JsonPointer.compile("/type")).textValue();
        log.debug("Found extractor type: '{}'", extractorTypeValue);
        // Create the requested extractor type.
        if (JDBC_EXTRACTOR.equalsIgnoreCase(extractorTypeValue)) {
            return new EtlJdbcDataExtractor(extractorConfig);
        } else {
            throw new EtlUnknownExtractorTypeException("Unknown extractor type found: '" + extractorTypeValue + "'.");
        }
    }

}
