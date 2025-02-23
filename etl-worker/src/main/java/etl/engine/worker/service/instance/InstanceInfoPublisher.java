package etl.engine.worker.service.instance;

import etl.engine.worker.model.messaging.EmsMessage;
import etl.engine.worker.model.messaging.Info;
import etl.engine.worker.model.messaging.InstanceInfoPayload;
import etl.engine.worker.service.messaging.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstanceInfoPublisher {

    @Value("${ews.status-reporting.enabled}")
    private boolean isReportingEnabled;

    private final InstanceInfoManager instanceInfoManager;
    private final MessagingService messagingService;

    @Scheduled(
            scheduler = "etlThreadSchedulerPool",
            initialDelayString = "${ews.status-reporting.initial-delay-ms}",
            fixedRateString = "${ews.status-reporting.fixed-rate-ms}")
    public void report() {
        if (isReportingEnabled) {
            Info info = new Info(Info.HEARTBEAT);
            InstanceInfoPayload payload = new InstanceInfoPayload(
                    instanceInfoManager.getInstanceId(),
                    instanceInfoManager.getInstanceState()
            );
            EmsMessage<Info, InstanceInfoPayload> message = new EmsMessage<>(info, payload);
            messagingService.publishInstanceInfo(message);
            log.info("The instance info has been published: {}",  message);
        }
    }

}
