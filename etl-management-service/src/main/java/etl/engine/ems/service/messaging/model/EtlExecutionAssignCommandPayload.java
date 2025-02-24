package etl.engine.ems.service.messaging.model;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Getter
@ToString
public class EtlExecutionAssignCommandPayload {

    private UUID etlExecutionId;
    private OffsetDateTime lastRunAt;
    private Object configuration;

}
