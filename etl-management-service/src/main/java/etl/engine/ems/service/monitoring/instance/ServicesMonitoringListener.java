package etl.engine.ems.service.monitoring.instance;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.ems.dao.entity.ServiceMonitoring;
import etl.engine.ems.dao.entity.ServiceStatus;
import etl.engine.ems.dao.repository.ServiceMonitoringRepository;
import etl.engine.ems.model.InstanceStatusReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * The component listens the topic and updates the table in the database to
 * reflect the actual services state.
 */
@Component
@KafkaListener(groupId = "${ems.kafka.consumer.group}", topics = "${ems.kafka.topics.heartbeat}")
@RequiredArgsConstructor
@Slf4j
public class ServicesMonitoringListener {

    private final static String INFO_TYPE_PTR = "/info/type";
    private final static String INFO_SIGNAL_PTR = "/info/signal";
    private final static String PAYLOAD_PTR = "/payload";
    private final static String NOTIFICATION_TYPE = "notification";
    private final static String SIGNAL_TYPE = "service-status";

    private final ObjectMapper mapper;
    private final ServiceMonitoringRepository serviceMonitoringRepository;

    @KafkaHandler
    @Transactional
    public void listener(String message) {
        log.debug("Raw message: {}", message);
        try {
            JsonNode document = mapper.readTree(message);
            JsonPointer messageTypePtr = JsonPointer.compile(INFO_TYPE_PTR);
            JsonPointer messageSignalPtr = JsonPointer.compile(INFO_SIGNAL_PTR);
            final String typeNodeValue = document.at(messageTypePtr).asText();
            final String signalNodeValue = document.at(messageSignalPtr).asText();
            if (NOTIFICATION_TYPE.equals(typeNodeValue)) {
                if (SIGNAL_TYPE.equals(signalNodeValue)) {
                    JsonPointer payloadPtr = JsonPointer.compile(PAYLOAD_PTR);
                    JsonNode payloadNode = document.at(payloadPtr);
                    InstanceStatusReport instanceStatusReport = mapper.treeToValue(payloadNode, InstanceStatusReport.class);
                    log.debug("Extracted payload: {}", instanceStatusReport);
                    ServiceMonitoring entity = new ServiceMonitoring();
                    entity.setId(instanceStatusReport.getInstanceId());
                    entity.setInstanceType(instanceStatusReport.getInstanceType());
                    entity.setInstanceState(instanceStatusReport.getInstanceState());
                    entity.setReportedAt(instanceStatusReport.getReportedAt());
                    entity.setStatus(ServiceStatus.online);
                    entity.setStatusUpdatedAt(LocalDateTime.now());
                    serviceMonitoringRepository.save(entity);
                    log.debug("The report was saved.");
                } else {
                    log.warn("Unknown signal value at {}. Found: '{}'. Expected: '{}'. The message ignored.",
                            INFO_SIGNAL_PTR,
                            SIGNAL_TYPE,
                            signalNodeValue);
                }
            } else {
                log.warn("Unknown type value at {}. Found: '{}'. Expected: '{}'. The message ignored.",
                        INFO_TYPE_PTR,
                        NOTIFICATION_TYPE,
                        typeNodeValue);
            }
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage(), e);
        }
    }

}
