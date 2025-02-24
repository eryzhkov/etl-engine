package etl.engine.ems.service.monitoring.instance;

import etl.engine.ems.dao.entity.EtlInstance;
import etl.engine.ems.dao.entity.ServiceStatus;
import etl.engine.ems.dao.repository.EtlInstanceRepository;
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
public class EtlWorkerStatusUpdater {

    @Value("${ems.monitoring.fixed-rate-ms}")
    private long monitoringFixedRateMs;

    @Value("${ems.monitoring.unknown-status-threshold-ms}")
    private long unknownStatusThresholdMs;

    @Value("${ems.monitoring.offline-status-threshold-ms}")
    private long offlineStatusThresholdMs;

    private final EtlInstanceRepository etlInstanceRepository;

    @Scheduled(
            scheduler = "etlThreadSchedulerPool",
            initialDelayString = "${ems.monitoring.initial-delay-ms}",
            fixedRateString = "${ems.monitoring.fixed-rate-ms}"
    )
    @Transactional("transactionManager")
    public void checkStatus() {
        log.debug("monitoringFixedRateMs = {}, unknownStatusThresholdMs = {}, offlineStatusThresholdMs = {}",
                monitoringFixedRateMs, unknownStatusThresholdMs, offlineStatusThresholdMs);
        List<EtlInstance> services = etlInstanceRepository.findAll();
        OffsetDateTime rightNow = OffsetDateTime.now();
        for (EtlInstance etlInstance : services) {
            log.debug("Check the ETL-Worker instance '{}'...", etlInstance.getId());
            log.debug("rightNow = {}", rightNow);
            log.debug("reportedAt = {}", etlInstance.getReportedAt());
            long timeDeltaMs = Duration.between(etlInstance.getReportedAt(), rightNow).toMillis();
            log.debug("timeDeltaMs = {}", timeDeltaMs);
            if (timeDeltaMs > monitoringFixedRateMs && timeDeltaMs <= unknownStatusThresholdMs) {
                etlInstance.setStatus(ServiceStatus.unknown);
                etlInstanceRepository.save(etlInstance);
                log.debug("For the ETL-Worker instance '{}' the status was changed from 'online' to '{}'.",
                        etlInstance.getId(), ServiceStatus.unknown);
            }
            if (timeDeltaMs > unknownStatusThresholdMs && timeDeltaMs <= offlineStatusThresholdMs) {
                etlInstance.setStatus(ServiceStatus.offline);
                etlInstanceRepository.save(etlInstance);
                log.debug("For the ETL-Worker instance '{}' the status was changed from '{}' to '{}'.",
                        etlInstance.getId(), ServiceStatus.unknown, ServiceStatus.offline);
            }
            if (timeDeltaMs > offlineStatusThresholdMs) {
                etlInstanceRepository.delete(etlInstance);
                log.debug("The ETL-Worker instance '{}' was removed from monitoring.", etlInstance.getId());
            }
        }
        log.debug("ETL-Workers statuses has been updated.");
    }

}
