package etl.engine.worker.model.messaging;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
@Builder
public class ProgressPayload {

    private final UUID assignee;
    private final UUID etlExecutionId;

}
