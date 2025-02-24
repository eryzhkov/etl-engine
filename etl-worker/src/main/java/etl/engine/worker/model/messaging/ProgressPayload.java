package etl.engine.worker.model.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class ProgressPayload {

    private final UUID etlExecutionId;

}
