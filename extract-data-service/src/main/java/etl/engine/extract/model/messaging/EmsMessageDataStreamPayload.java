package etl.engine.extract.model.messaging;

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
public class EmsMessageDataStreamPayload {

    private final UUID etlExecutionId;
    private final String phase;
    private final String dataStreamName;
    private final UUID instanceId;
    private final String error;

}
