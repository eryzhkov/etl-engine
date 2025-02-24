package etl.engine.worker.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.worker.exception.InvalidNodeTypeException;
import etl.engine.worker.exception.InvalidNodeValueException;
import etl.engine.worker.exception.MissingNodeException;
import etl.engine.worker.model.messaging.Info;
import etl.engine.worker.service.execution.EtlExecutionManager;
import etl.engine.worker.service.instance.InstanceInfoManager;
import etl.engine.worker.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@KafkaListener(groupId = "${ews.kafka.consumer.group}-#{instanceInfoManager.instanceId}", topics = "${ews.kafka.topics.control}")
@RequiredArgsConstructor
@Slf4j
public class ControlTopicListener {

    private final static String INFO_TYPE_PATH = "/info/type";
    private final static String ASSIGNEE_PATH = "/info/assignee";
    private final static String EXECUTION_ID_PATH = "/payload/etlExecutionId";
    private final static String LAST_RUN_AT_PATH = "/payload/lastRunAt";
    private final static String CONFIGURATION_PATH = "/payload/configuration";

    private final ObjectMapper mapper;
    private final MessagingService messagingService;
    private final InstanceInfoManager instanceInfoManager;
    private final EtlExecutionManager etlExecutionManager;

    @KafkaHandler
    @Transactional("kafkaTransactionManager")
    public void listener(String message) {
        log.debug("{}", message);
        try {
            JsonNode document = mapper.readTree(message);
            final String infoType = JsonUtils.readValueAsString(document, INFO_TYPE_PATH);
            if (Info.ETL_EXECUTION_ASSIGN.equalsIgnoreCase(infoType)) {
                final UUID assignee = JsonUtils.readValueAsUUID(document, ASSIGNEE_PATH);
                if (instanceInfoManager.getInstanceId().equals(assignee)) {
                    final UUID etlExecutionId = JsonUtils.readValueAsUUID(document, EXECUTION_ID_PATH);
                    if (instanceInfoManager.isIdle()) {
                        //TODO Does it make sense analyze interval between messages' timestamp and the current date-time? The idea is reject too old commands.
                        final OffsetDateTime lastRunAt = JsonUtils.readValueAsOffsetDateTime(document, LAST_RUN_AT_PATH);
                        final JsonNode configurationNode = JsonUtils.getNode(document, CONFIGURATION_PATH);
                        log.info("The ETL-execution with id = '{}' and lastRunAt = '{}' was assigned to me.", etlExecutionId, lastRunAt);
                        messagingService.acceptEtlExecution(etlExecutionId);
                        log.info("The ETL-execution with id = '{}' is accepted. The accepted message published.", etlExecutionId);
                        etlExecutionManager.runEtlExecutionAsync(etlExecutionId, lastRunAt, configurationNode);
                        log.info("The ETL-execution is run in a separated thread.");
                    } else {
                        // For unknown reason the instance is busy - reject the assignment.
                        messagingService.rejectEtlExecution(etlExecutionId);
                        log.warn("I'm busy right now - the assignment is rejected: {}", instanceInfoManager.getWorkload());
                    }

                } else {
                    log.info("The ETL-execution assignee identifier '{}' differs from the instance identifier. I'm not the assignee. The message is skipped.", assignee);
                }
            } else {
                log.warn("Unsupported command type '{}' found. The command is skipped.", infoType);
            }
        } catch (JsonProcessingException | MissingNodeException | InvalidNodeTypeException | InvalidNodeValueException e) {
            log.error("Invalid message found: {}. The message is skipped.", e.getMessage(), e);
        }
    }

}
