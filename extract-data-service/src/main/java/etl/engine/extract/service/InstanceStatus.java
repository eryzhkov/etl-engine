package etl.engine.extract.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The component is the holder of the instance status information. The information is exposed via /actuator/info and
 * is used to create the status report.
 */
@Component
public class InstanceStatus {

    private final UUID INSTANCE_ID = UUID.randomUUID();

    public UUID getInstanceId() {
        return INSTANCE_ID;
    }
}
