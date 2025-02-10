package etl.engine.extract.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.ToString.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class EtlExecutionInfo {

    private final UUID executionId;
    private final String externalSystemCode;
    private final String etlProcessCode;
    private final Map<String, DataStreamInfo> dataStreamsInfo = new HashMap<>();
    @Exclude
    @JsonIgnore
    private final JsonNode configuration;

}
