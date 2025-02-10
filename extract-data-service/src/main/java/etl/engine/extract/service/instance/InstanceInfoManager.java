package etl.engine.extract.service.instance;

import etl.engine.extract.model.EtlExecutionInfo;
import etl.engine.extract.model.InstanceState;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The component is the holder of the instance status information. The information is exposed via /actuator/info and
 * is used to create the status report.
 * @see etl.engine.extract.service.instance.InstanceStatusInfoContributor
 */
@Component
public class InstanceInfoManager {

    private final UUID INSTANCE_ID = UUID.randomUUID();

    private final Map<UUID, EtlExecutionInfo> etlExecutionsInfo = new ConcurrentHashMap<>();

    public UUID getInstanceId() {
        return INSTANCE_ID;
    }

    public String getType() {
        // The default instance type for the 'extract-data-service'.
        return "DATA_EXTRACTOR";
    }

    public String getPhase() {
        return "data-extract";
    }

    public String getState() {
        if (etlExecutionsInfo.isEmpty()) {
            return InstanceState.idle.toString();
        } else {
            return InstanceState.busy.toString();
        }
    }

    public void addEtlExecutionInfo(EtlExecutionInfo etlExecutionInfo) {
        etlExecutionsInfo.putIfAbsent(etlExecutionInfo.getExecutionId(), etlExecutionInfo);
    }

    public void replaceEtlExecutionInfo(EtlExecutionInfo etlExecutionInfo) {
        etlExecutionsInfo.replace(etlExecutionInfo.getExecutionId(), etlExecutionInfo);
    }

    public void deleteEtlExecutionInfo(UUID etlExecutionId) {
        etlExecutionsInfo.remove(etlExecutionId);
    }

    public Collection<EtlExecutionInfo> getWorkload() {
        return etlExecutionsInfo.values();
    }
}
