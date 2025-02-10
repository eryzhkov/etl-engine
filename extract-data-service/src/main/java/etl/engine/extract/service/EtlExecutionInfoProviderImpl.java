package etl.engine.extract.service;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.extract.exception.EtlConfigurationLoadException;
import etl.engine.extract.exception.EtlConfigurationParseException;
import etl.engine.extract.model.DataStreamInfo;
import etl.engine.extract.model.EtlExecutionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionInfoProviderImpl implements EtlExecutionInfoProvider {

    private final RestClient restClient;
    private final ObjectMapper mapper;

    @Override
    public EtlExecutionInfo getExecutionInfo(String externalSystemCode, String etlProcessCode, UUID etlExecutionId)
            throws EtlConfigurationParseException, EtlConfigurationLoadException {
        log.debug("Provide ETL-execution info for external system '{}', ETL-process '{}', ETL-execution '{}'.",
                externalSystemCode, etlProcessCode, etlExecutionId);
        JsonNode etlConfiguration = loadConfiguration(externalSystemCode, etlProcessCode, etlExecutionId);
        EtlExecutionInfo etlExecutionInfo = new EtlExecutionInfo(
                etlExecutionId,
                externalSystemCode,
                etlProcessCode,
                etlConfiguration
        );
        log.debug("ETL-configuration is loaded. Try to parse data streams.");
        Map<String, DataStreamInfo> dataStreamsInfo = etlExecutionInfo.getDataStreamsInfo();
        JsonNode dataStreams = etlConfiguration.at(JsonPointer.compile("/data/streams"));
        for(JsonNode dataStream : dataStreams) {
            String dataStreamCode = dataStream.at(JsonPointer.compile("/code")).asText();
            DataStreamInfo dataStreamInfo = new DataStreamInfo(dataStreamCode, dataStream);
            dataStreamsInfo.putIfAbsent(dataStreamCode, dataStreamInfo);
            log.debug("Added configuration for the data stream '{}'.", dataStreamCode);
        }
        log.debug("EtlExecutionInfo is ready.");
        return etlExecutionInfo;
    }

    private JsonNode loadConfiguration(String externalSystemCode, String etlProcessCode, UUID etlExecutionId) throws EtlConfigurationLoadException, EtlConfigurationParseException {
        log.debug("Load ETL-configuration via REST API for external system '{}', ETL-process '{}', ETL-execution '{}'.",
                externalSystemCode, etlProcessCode, etlExecutionId);
        JsonNode configuration;
        try {
            ResponseEntity<String> responseEntity = restClient
                    .get()
                    .uri("/api/v1/etl-configs/0/0/data-extract")
                    .retrieve()
                    .toEntity(String.class);
            log.debug("EMS response with ETL-configuration: {}", responseEntity.getBody());
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                JsonNode response = mapper.readTree(responseEntity.getBody());
                configuration = response.at(JsonPointer.compile("/payload"));
            } else {
                throw new EtlConfigurationLoadException("Load the ETL-configuration failed with unexpected result: "
                        + responseEntity.getBody());
            }
        } catch (RestClientException e) {
            log.error("{}", e.getMessage(), e);
            throw new EtlConfigurationLoadException("Load the ETL-configuration failed: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage(), e);
            throw new EtlConfigurationParseException("Read the ETL-configuration failed: " + e.getMessage(), e);
        }
        return configuration;
    }

}
