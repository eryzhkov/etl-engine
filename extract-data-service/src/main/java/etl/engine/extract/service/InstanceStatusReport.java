package etl.engine.extract.service;

import etl.engine.extract.model.event.Event;
import etl.engine.extract.model.event.EventInfo;
import etl.engine.extract.model.workload.Workload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * The component is responsible for the instance status report generation with the predefined rate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InstanceStatusReport {

    private final InstanceStatus instanceStatus;

    @Scheduled(
            initialDelayString = "${eds.status-report.initial-delay}",
            fixedRateString = "${eds.status-report.fixed-rate}")
    public void report() {
        InstanceStatusReportPayload payload = new InstanceStatusReportPayload(
                instanceStatus.getInstanceId(),
                instanceStatus.getType(),
                instanceStatus.getState(),
                instanceStatus.getWorkload(),
                LocalDateTime.now()
        );
        Event<InstanceStatusReportPayload> event = new Event<>(
                new EventInfo("notification", "status"),
                payload
        );
        log.info("The report is generated: {}", event);
    }

    @AllArgsConstructor
    @Getter
    @ToString
    static class InstanceStatusReportPayload {
        private UUID id;
        private String type;
        private String state;
        private List<Workload> workload;
        private LocalDateTime timestamp;
    }

}
