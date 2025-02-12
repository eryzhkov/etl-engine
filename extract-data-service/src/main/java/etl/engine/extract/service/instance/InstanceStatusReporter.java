package etl.engine.extract.service.instance;

import etl.engine.extract.model.messaging.EmsMessage;
import etl.engine.extract.model.messaging.EmsMessageInfo;
import etl.engine.extract.model.messaging.EmsMessageInstanceStatusPayload;
import etl.engine.extract.service.messaging.MessagingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The component is responsible for the instance status report generation with the fixed rate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InstanceStatusReporter {

    @Value("${eds.status-reporting.enabled}")
    private boolean isReportingEnabled;
    private final InstanceInfoManager instanceInfoManager;
    private final MessagingService messagingService;

    @Scheduled(
            initialDelayString = "${eds.status-reporting.initial-delay-ms}",
            fixedRateString = "${eds.status-reporting.fixed-rate-ms}")
    public void report() {
        if (isReportingEnabled) {
            EmsMessageInfo info = new EmsMessageInfo(EmsMessageInfo.INSTANCE_STATUS);
            EmsMessageInstanceStatusPayload payload = new EmsMessageInstanceStatusPayload(
                    instanceInfoManager.getInstanceId(),
                    instanceInfoManager.getType(),
                    instanceInfoManager.getState(),
                    instanceInfoManager.getWorkload()
            );
            EmsMessage<EmsMessageInfo, EmsMessageInstanceStatusPayload> message = new EmsMessage<>(info, payload);
            messagingService.publishInstanceStatus(message);
            log.info("The report is generated and sent: {}", message);
        }
    }

    @PostConstruct
    void postConstruct() {
        if (isReportingEnabled) {
            log.info("The instance status reporting is enabled.");
        } else {
            log.info("The instance status reporting is disabled.");
        }
    }

}
