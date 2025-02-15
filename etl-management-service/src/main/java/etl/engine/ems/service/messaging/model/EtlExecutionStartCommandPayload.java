package etl.engine.ems.service.messaging.model;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@ToString
public class EtlExecutionStartCommandPayload {

    private UUID etlExecutionId;
    private String externalSystemCode;
    private String etlProcessCode;

}
