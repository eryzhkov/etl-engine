package etl.engine.extract.service.messaging.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class EtlExecutionStartCommandPayload {

    private final UUID etlExecutionId;
    private final String externalSystemCode;
    private final String etlProcessCode;

}
