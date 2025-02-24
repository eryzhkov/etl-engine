package etl.engine.worker.model.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ProgressPayload {

    protected final UUID assignee;
    protected final UUID etlExecutionId;

}
