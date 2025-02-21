package etl.engine.worker.service.instance;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The component is responsible for building the "ews" object in the /actuator/info.
 */
@Component
@RequiredArgsConstructor
public class InstanceInfoContributor implements InfoContributor {

    private final InstanceInfoManager instanceInfoManager;

    @Override
    public void contribute(Builder builder) {
        Map<String, Object> instanceInfo = new LinkedHashMap<>();
        instanceInfo.put("instanceId", instanceInfoManager.getInstanceId().toString());
        builder.withDetail("ews", instanceInfo);
    }

}
