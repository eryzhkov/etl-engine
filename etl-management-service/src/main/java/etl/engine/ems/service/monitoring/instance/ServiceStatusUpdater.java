package etl.engine.ems.service.monitoring.instance;

import etl.engine.ems.dao.entity.ServiceMonitoring;
import etl.engine.ems.dao.entity.ServiceStatus;
import etl.engine.ems.dao.repository.ServiceMonitoringRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * The component updates service statuses in the 'service_monitoring' table.
 * The logic is based on comparing the service last reported state timestamp and the current time.
 * A service status can be one of the following: online, unknown, offline.
 * If a service is in the 'offline' status too long it will be removed from the table.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceStatusUpdater {

    @Value("${ems.monitoring.fixed-rate-ms}")
    private long monitoringFixedRateMs;

    @Value("${ems.monitoring.unknown-status-threshold-ms}")
    private long unknownStatusThresholdMs;

    @Value("${ems.monitoring.offline-status-threshold-ms}")
    private long offlineStatusThresholdMs;

    private final ServiceMonitoringRepository serviceMonitoringRepository;

    @Scheduled(
            initialDelayString = "${ems.monitoring.initial-delay-ms}",
            fixedRateString = "${ems.monitoring.fixed-rate-ms}"
    )
    @Transactional
    public void checkStatus() {
        log.debug("monitoringFixedRateMs = {}, unknownStatusThresholdMs = {}, offlineStatusThresholdMs = {}",
                monitoringFixedRateMs, unknownStatusThresholdMs, offlineStatusThresholdMs);
        List<ServiceMonitoring> services = serviceMonitoringRepository.findAll();
        OffsetDateTime rightNow = OffsetDateTime.now();
        for (ServiceMonitoring serviceMonitoring : services) {
            log.debug("Check the instance '{}'...", serviceMonitoring.getId());
            log.debug("rightNow = {}", rightNow);
            log.debug("reportedAt = {}", serviceMonitoring.getReportedAt());
            long timeDeltaMs = Duration.between(serviceMonitoring.getReportedAt(), rightNow).toMillis();
            log.debug("timeDeltaMs = {}", timeDeltaMs);
            if (timeDeltaMs > monitoringFixedRateMs && timeDeltaMs <= unknownStatusThresholdMs) {
                serviceMonitoring.setStatus(ServiceStatus.unknown);
                serviceMonitoringRepository.save(serviceMonitoring);
                log.debug("For the instance '{}' the status was changed from 'online' to '{}'.",
                        serviceMonitoring.getId(), ServiceStatus.unknown);
            }
            if (timeDeltaMs > unknownStatusThresholdMs && timeDeltaMs <= offlineStatusThresholdMs) {
                serviceMonitoring.setStatus(ServiceStatus.offline);
                serviceMonitoringRepository.save(serviceMonitoring);
                log.debug("For the instance '{}' the status was changed from '{}' to '{}'.",
                        serviceMonitoring.getId(), ServiceStatus.unknown, ServiceStatus.offline);
            }
            if (timeDeltaMs > offlineStatusThresholdMs) {
                serviceMonitoringRepository.delete(serviceMonitoring);
                log.debug("The instance '{}' was removed from monitoring.", serviceMonitoring.getId());
            }
        }
        log.debug("Service statuses were checked.");
    }

}
