package etl.engine.extract.model.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class EmsMessageEtlExecutionStartPayload {

    private final UUID etlExecutionId;
    private final String externalSystemCode;
    private final String etlProcessCode;

}
