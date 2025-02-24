package etl.engine.worker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString
@Builder
public class EtlExecutionInfo {

    private UUID etlExecutionId;
    private OffsetDateTime startedAt;

}
