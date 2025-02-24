package etl.engine.worker.service.instance;

import etl.engine.worker.model.InstanceState;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The component is the holder of the instance information.
 */
@Component
@Slf4j
public class InstanceInfoManager {

    private final UUID INSTANCE_ID = UUID.randomUUID();

    public UUID getInstanceId() {
        return INSTANCE_ID;
    }

    public String getInstanceState() {
        return InstanceState.idle.toString();
    }

    @PostConstruct
    public void postConstruct() {
        log.info("The instance ID = {}", INSTANCE_ID);
    }

}
