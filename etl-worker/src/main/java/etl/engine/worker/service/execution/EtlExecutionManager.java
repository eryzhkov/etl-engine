package etl.engine.worker.service.execution;

import com.fasterxml.jackson.databind.JsonNode;
import etl.engine.worker.exception.InvalidEtlConfigurationException;
import etl.engine.worker.model.EtlExecutionInfo;
import etl.engine.worker.service.instance.InstanceInfoManager;
import etl.engine.worker.service.messaging.MessagingService;
import etl.engine.worker.validator.EtlConfigurationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionManager {

    private final MessagingService messagingService;
    private final InstanceInfoManager instanceInfoManager;

    @Async("etlThreadAsyncPool")
    public void runEtlExecutionAsync(UUID etlExecutionId, OffsetDateTime lastRunAt, JsonNode configuration) {
        log.info("ENTER");
        log.debug("IN: etlExecutionId='{}', lastRunAt='{}', configuration={}", etlExecutionId, lastRunAt, configuration);
        messagingService.startEtlExecution(etlExecutionId);
        instanceInfoManager.takeWorkload(
                EtlExecutionInfo.builder()
                        .etlExecutionId(etlExecutionId)
                        .startedAt(OffsetDateTime.now())
                        .build()
        );
        try {
            EtlConfigurationValidator.validate(configuration);
            messagingService.finishEtlExecution(etlExecutionId);
        } catch (InvalidEtlConfigurationException e) {
            log.error("The execution is failed: {}.", e.getMessage());
            messagingService.failEtlExecution(etlExecutionId, e.getMessage());
        } finally {
            instanceInfoManager.dropWorkload();
        }
        log.info("EXIT");
    }

}
