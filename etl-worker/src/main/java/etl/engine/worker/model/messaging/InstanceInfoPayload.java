package etl.engine.worker.model.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class InstanceInfoPayload {

    private final UUID id;
    private final String state;

}
