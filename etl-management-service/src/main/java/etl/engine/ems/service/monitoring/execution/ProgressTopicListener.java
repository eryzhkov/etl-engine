package etl.engine.ems.service.monitoring.execution;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.service.EtlDataStreamExecutionService;
import etl.engine.ems.service.EtlExecutionService;
import etl.engine.ems.service.messaging.model.EtlNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@KafkaListener(groupId = "${ems.kafka.consumer.group}", topics = "${ems.kafka.topics.progress}")
@RequiredArgsConstructor
@Slf4j
public class ProgressTopicListener {

    private final ObjectMapper mapper;
    private final EtlExecutionService etlExecutionService;
    private final EtlDataStreamExecutionService etlDataStreamExecutionService;

    @KafkaHandler
    @Transactional("kafkaTransactionManager")
    public void listener(String message) {
        log.debug("The received message: {}", message);
        try {
            JsonNode document = mapper.readTree(message);
            String notification = document.at(JsonPointer.compile("/info/type")).asText();
            UUID etlExecutionId = getExecutionId(document);
            OffsetDateTime timestamp = getTimestamp(document);
            log.debug("Extracted notification='{}', timestamp='{}', etlExecutionId='{}'.",
                    notification, timestamp, etlExecutionId);
            if (notification != null) {
                if (EtlNotification.ETL_EXECUTION_ACCEPTED.equalsIgnoreCase(notification)) {
                    // Handle etl-execution-accepted
                    etlExecutionService.markEtlExecutionAsAcceptedAt(etlExecutionId, timestamp);
                    log.debug("The ETL-execution with id = '{}' was accepted by the worker.", etlExecutionId);
                } else if (EtlNotification.ETL_EXECUTION_STARTED.equalsIgnoreCase(notification)) {
                    // Handle etl-execution-started
                    etlExecutionService.markEtlExecutionAsStartedAt(etlExecutionId, timestamp);
                    log.debug("The ETL-execution with id = '{}' was started by the worker.", etlExecutionId);
                } else if (EtlNotification.ETL_EXECUTION_FINISHED.equalsIgnoreCase(notification)) {
                    // Handle etl-execution-finished
                    etlExecutionService.markEtlExecutionAsFinishedAt(etlExecutionId, timestamp);
                    log.debug("The ETL-execution with id = '{}' was finished by the worker.", etlExecutionId);
                } else if (EtlNotification.ETL_EXECUTION_FAILED.equalsIgnoreCase(notification)) {
                    // Handle etl-execution-failed
                    etlExecutionService.markEtlExecutionAsFailedAt(etlExecutionId, timestamp, getMessage(document));
                    log.debug("The ETL-execution with id = '{}' was failed by the worker.", etlExecutionId);
                } else if (EtlNotification.ETL_DATA_STREAM_STARTED.equalsIgnoreCase(notification)) {
                    etlDataStreamExecutionService.startEtlDataStream(
                            etlExecutionId,
                            readValueAsString(document, "/payload/phase"),
                            readValueAsString(document, "/payload/dataStreamName"),
                            readValueAsUUID(document, "/payload/instanceId"),
                            timestamp);
                } else if (EtlNotification.ETL_DATA_STREAM_FINISHED.equalsIgnoreCase(notification)) {
                    etlDataStreamExecutionService.finishEtlDataStream(
                            etlExecutionId,
                            readValueAsString(document, "/payload/phase"),
                            readValueAsString(document, "/payload/dataStreamName"),
                            timestamp
                    );
                } else if (EtlNotification.ETL_DATA_STREAM_FAILED.equalsIgnoreCase(notification)) {
                    etlDataStreamExecutionService.failEtlDataStream(
                            etlExecutionId,
                            readValueAsString(document, "/payload/phase"),
                            readValueAsString(document, "/payload/dataStreamName"),
                            timestamp,
                            readValueAsString(document, "/payload/error")
                    );
                } else if (EtlNotification.ETL_DATA_STREAM_STATS.equalsIgnoreCase(notification)) {
                    etlDataStreamExecutionService.updateEtlDataStreamStats(
                            etlExecutionId,
                            readValueAsString(document, "/payload/phase"),
                            readValueAsString(document, "/payload/dataStreamName"),
                            readValueAsLong(document, "/payload/totalInMessages"),
                            readValueAsLong(document, "/payload/totalOutMessages"),
                            readValueAsLong(document, "/payload/totalFailedMessages")
                    );
                } else {
                    log.warn("Unsupported notification type found '{}'. The message is ignored.", notification);
                }
            } else {
                log.warn("Found null notification type. The message is ignored.");
            }
        } catch (JsonProcessingException | EntityNotFoundException e) {
            log.error("The message is ignored due to the error: {}", e.getMessage(), e);
        }
    }

    private OffsetDateTime getTimestamp(JsonNode document) {
        String value = document.at(JsonPointer.compile("/info/timestamp")).asText();
        return OffsetDateTime.parse(value);
    }

    private UUID getExecutionId(JsonNode document) {
        String value = document.at(JsonPointer.compile("/payload/etlExecutionId")).asText();
        return UUID.fromString(value);
    }

    private String getMessage(JsonNode document) {
        return document.at(JsonPointer.compile("/payload/message")).asText();
    }

    private String readValueAsString(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asText();
    }

    private UUID readValueAsUUID(JsonNode document, String path) {
        String value = document.at(JsonPointer.compile(path)).asText();
        return UUID.fromString(value);
    }

    private long readValueAsLong(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asLong();
    }

}
