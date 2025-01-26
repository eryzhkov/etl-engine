package etl.engine.extract.service;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The component is responsible for building the "eds" object in the /actuator/info.
 */
@Component
public class InstanceStatusInfoContributor implements InfoContributor {

    private final InstanceStatus instanceStatus;

    public InstanceStatusInfoContributor(InstanceStatus instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    @Override
    public void contribute(Builder builder) {
        Map<String, String> instanceInfo = new LinkedHashMap<>();
        instanceInfo.put("instanceId", instanceStatus.getInstanceId().toString());
        builder.withDetail("eds", instanceInfo);
    }
}
