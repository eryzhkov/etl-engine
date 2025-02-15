package etl.engine.ems.service.messaging.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@ToString
@Getter
public class EtlExecutionPayload {

    private final UUID etlExecutionId;

}
