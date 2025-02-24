package etl.engine.worker.model.messaging;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class FailedProgressPayload extends ProgressPayload {

    private final String reason;

    @Builder(builderMethodName = "failedBuilder")
    public FailedProgressPayload(UUID assignee, UUID etlExecutionId, String reason) {
        super(assignee, etlExecutionId);
        this.reason = reason;
    }

}
