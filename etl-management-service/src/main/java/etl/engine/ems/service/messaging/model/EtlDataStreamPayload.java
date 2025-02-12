package etl.engine.ems.service.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@ToString
@Getter
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtlDataStreamPayload {
    private final UUID etlExecutionId;
    private final String phase;
    private final String dataStreamName;
    private final UUID instanceId;
    private final String error;
}
