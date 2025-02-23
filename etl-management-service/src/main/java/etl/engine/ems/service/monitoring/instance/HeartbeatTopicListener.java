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
public class HeartbeatTopicListener {

    private final static String INFO_TYPE_PTR = "/info/type";
    private final static String INFO_TIMESTAMP_PTR = "/info/timestamp";
    private final static String PAYLOAD_PTR = "/payload";
    private final static String HEARTBEAT = "heartbeat";

    private final ObjectMapper mapper;
    private final EtlInstanceService etlInstanceService;

    @KafkaHandler
    @Transactional("kafkaTransactionManager")
    public void listener(String message) {
        log.debug("Raw message: {}", message);
        try {
            JsonNode document = mapper.readTree(message);
            JsonPointer infoTypePtr = JsonPointer.compile(INFO_TYPE_PTR);
            JsonPointer infoTimestampPtr = JsonPointer.compile(INFO_TIMESTAMP_PTR);
            final String infoType = document.at(infoTypePtr).asText();
            final String timestampValue = document.at(infoTimestampPtr).asText();
            log.debug("notification = '{}'", infoType);
            log.debug("timestamp = '{}'", timestampValue);
            if (HEARTBEAT.equals(infoType)) {
                    JsonPointer payloadPtr = JsonPointer.compile(PAYLOAD_PTR);
                    JsonNode payloadNode = document.at(payloadPtr);
                    InstanceStatusReport instanceStatusReport = mapper.treeToValue(payloadNode, InstanceStatusReport.class);
                    instanceStatusReport.setReportedAt(OffsetDateTime.parse(timestampValue));
                    log.debug("Extracted payload: {}", instanceStatusReport);
                    etlInstanceService.saveInstanceStatusReport(instanceStatusReport);
                    log.debug("The report was saved.");
            } else {
                log.warn("Unknown notification value at '{}'. Found: '{}'. Expected: '{}'. The message was ignored.",
                        INFO_TYPE_PTR,
                        HEARTBEAT,
                        infoType);
            }
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage(), e);
        }
    }

}
