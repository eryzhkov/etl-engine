package etl.engine.worker.service.instance;

import etl.engine.worker.model.EtlExecutionInfo;
import etl.engine.worker.model.InstanceState;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The component is the holder of the instance information.
 */
@Component
@Slf4j
public class InstanceInfoManager {

    private final UUID INSTANCE_ID = UUID.randomUUID();

    //TODO Return unmodifiable or event better immutable Map in the future
    @Getter
    private final Map<UUID, EtlExecutionInfo> workload = new ConcurrentHashMap<>();

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

    public boolean isIdle() {
        return workload.isEmpty();
    }

    public void takeWorkload(EtlExecutionInfo etlExecutionInfo) {
        if (workload.isEmpty()) {
            log.debug("Add a workload: {}", etlExecutionInfo);
            workload.put(etlExecutionInfo.getEtlExecutionId(), etlExecutionInfo);
        }
    }

    public void dropWorkload() {
        workload.clear();
        log.debug("Remove workload");
    }

}
