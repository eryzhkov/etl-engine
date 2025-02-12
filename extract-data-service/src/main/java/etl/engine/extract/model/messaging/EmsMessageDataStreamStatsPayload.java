package etl.engine.extract.model.messaging;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
@Getter
public class EmsMessageDataStreamStatsPayload extends EmsMessageDataStreamPayload {

    private final long totalInMessages;
    private final long totalOutMessages;
    private final long totalFailedMessages;

    public EmsMessageDataStreamStatsPayload(UUID etlExecutionId, String phase, String dataStreamName,
            UUID instanceId, String error, long totalInMessages, long totalOutMessages,
            long totalFailedMessages) {
        super(etlExecutionId, phase, dataStreamName, instanceId, error);
        this.totalInMessages = totalInMessages;
        this.totalOutMessages = totalOutMessages;
        this.totalFailedMessages = totalFailedMessages;
    }
}
