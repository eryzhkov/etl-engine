package etl.engine.extract.service.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import etl.engine.extract.exception.EtlExtractDataException;
import etl.engine.extract.model.EtlStreamData;

public interface EtlDataExtractor {

    EtlStreamData extractData(JsonNode dataStreamConfig) throws EtlExtractDataException;

}
