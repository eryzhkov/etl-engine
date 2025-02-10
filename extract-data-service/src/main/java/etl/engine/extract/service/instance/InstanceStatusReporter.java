package etl.engine.extract.service.instance;

import etl.engine.extract.model.EtlExecutionInfo;
import etl.engine.extract.model.event.Event;
import etl.engine.extract.model.event.EventInfo;
import etl.engine.extract.service.messaging.MessagingService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

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
            InstanceStatusReport statusReport = new InstanceStatusReport(
                    instanceInfoManager.getInstanceId(),
                    instanceInfoManager.getType(),
                    instanceInfoManager.getState(),
                    instanceInfoManager.getWorkload()
            );
            Event<InstanceStatusReport> event = new Event<>(
                    new EventInfo(EventInfo.NOTIFICATION_INSTANCE_STATUS),
                    statusReport
            );
            messagingService.sendInstanceStatusReport(event);
            log.info("The report is generated and sent: {}", event);
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

    @AllArgsConstructor
    @Getter
    @ToString
    static class InstanceStatusReport {
        private UUID id;
        private String type;
        private String state;
        private Collection<EtlExecutionInfo> workload;
    }

}
