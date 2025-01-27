package etl.engine.extract.service.instance;

import etl.engine.extract.model.workload.Workload;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The component is the holder of the instance status information. The information is exposed via /actuator/info and
 * is used to create the status report.
 * @see etl.engine.extract.service.instance.InstanceStatusInfoContributor
 */
@Component
public class InstanceStatus {

    private final UUID INSTANCE_ID = UUID.randomUUID();

    public UUID getInstanceId() {
        return INSTANCE_ID;
    }

    public String getType() {
        // The default instance type.
        return "extract";
    }

    public String getState() {
        // At the moment the instance can report the 'idle' state only.
        return "idle";
    }

    public List<Workload> getWorkload() {
        // At the moment the instance can report no workload.
        return Collections.emptyList();
    }
}
