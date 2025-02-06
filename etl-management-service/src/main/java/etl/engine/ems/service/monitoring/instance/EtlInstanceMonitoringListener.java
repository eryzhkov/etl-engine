package etl.engine.ems.service.monitoring.instance;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.ems.model.InstanceStatusReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * The component listens the topic and updates the table in the database to
 * reflect the actual services state.
 */
@Component
@KafkaListener(groupId = "${ems.kafka.consumer.group}", topics = "${ems.kafka.topics.heartbeat}")
@RequiredArgsConstructor
@Slf4j
public class EtlInstanceMonitoringListener {

    private final static String INFO_NOTIFICATION_PTR = "/info/notification";
    private final static String INFO_TIMESTAMP_PTR = "/info/timestamp";
    private final static String PAYLOAD_PTR = "/payload";
    private final static String NOTIFICATION = "instance-status";

    private final ObjectMapper mapper;
    private final EtlInstanceService etlInstanceService;

    @KafkaHandler
    @Transactional("kafkaTransactionManager")
    public void listener(String message) {
        log.debug("Raw message: {}", message);
        try {
            JsonNode document = mapper.readTree(message);
            JsonPointer infoNotificationPtr = JsonPointer.compile(INFO_NOTIFICATION_PTR);
            JsonPointer infoTimestampPtr = JsonPointer.compile(INFO_TIMESTAMP_PTR);
            final String notificationValue = document.at(infoNotificationPtr).asText();
            final String timestampValue = document.at(infoTimestampPtr).asText();
            log.debug("notification = '{}'", notificationValue);
            log.debug("timestamp = '{}'", timestampValue);
            if (NOTIFICATION.equals(notificationValue)) {
                    JsonPointer payloadPtr = JsonPointer.compile(PAYLOAD_PTR);
                    JsonNode payloadNode = document.at(payloadPtr);
                    InstanceStatusReport instanceStatusReport = mapper.treeToValue(payloadNode, InstanceStatusReport.class);
                    instanceStatusReport.setReportedAt(OffsetDateTime.parse(timestampValue));
                    log.debug("Extracted payload: {}", instanceStatusReport);
                    etlInstanceService.saveInstanceStatusReport(instanceStatusReport);
                    log.debug("The report was saved.");
            } else {
                log.warn("Unknown notification value at '{}'. Found: '{}'. Expected: '{}'. The message was ignored.",
                        INFO_NOTIFICATION_PTR,
                        NOTIFICATION,
                        notificationValue);
            }
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage(), e);
        }
    }

}
