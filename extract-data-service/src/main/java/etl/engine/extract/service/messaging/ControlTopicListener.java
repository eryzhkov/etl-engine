package etl.engine.extract.service.messaging;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.extract.service.EtlExecutionManager;
import etl.engine.extract.service.instance.InstanceInfoManager;
import etl.engine.extract.service.messaging.model.CommandInfo;
import etl.engine.extract.service.messaging.model.EtlExecutionStartCommandPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@KafkaListener(groupId = "${eds.kafka.consumer.group}", topics = "${eds.kafka.topics.control}")
@RequiredArgsConstructor
@Slf4j
public class ControlTopicListener {

    private final static String INFO_COMMAND_PTR = "/info/command";
    private final static String INFO_RECIPIENT_PTR = "/info/recipientInstanceId";
    private final static String PAYLOAD_PTR = "/payload";

    private final InstanceInfoManager instanceInfoManager;
    private final ObjectMapper mapper;
    private final EtlExecutionManager etlExecutionManager;

    @KafkaHandler
    @Transactional("kafkaTransactionManager")
    public void listener(String message) {
        log.debug("Got a command: {}", message);
        try {
            JsonNode document = mapper.readTree(message);
            JsonPointer recipientInstanceIdPtr = JsonPointer.compile(INFO_RECIPIENT_PTR);
            final String recipientInstanceId = document.at(recipientInstanceIdPtr).asText();
            log.debug("recipientInstanceId = '{}'", recipientInstanceId);
            log.debug("instanceId = '{}'", instanceInfoManager.getInstanceId());
            if (instanceInfoManager.getInstanceId().toString().equals(recipientInstanceId)) {
                log.info("The received command should be processed in the instance.");
                JsonPointer infoCommandPtr = JsonPointer.compile(INFO_COMMAND_PTR);
                final String commandValue = document.at(infoCommandPtr).asText();
                if (CommandInfo.ETL_EXECUTION_START.equals(commandValue)) {
                    log.debug("Found command: '{}'", commandValue);
                    JsonPointer payloadPtr = JsonPointer.compile(PAYLOAD_PTR);
                    JsonNode payloadNode = document.at(payloadPtr);
                    EtlExecutionStartCommandPayload commandPayload = mapper.treeToValue(payloadNode, EtlExecutionStartCommandPayload.class);
                    log.debug("Extracted command payload: {}", commandPayload);
                    etlExecutionManager.runEtlExecution(commandPayload);
                } else {
                    log.warn("The unknown command was found - '{}'. The messages is ignored.", commandValue);
                }
            } else {
                log.info("The received command is not for the instance and ignored. The recipientInstanceId={} but the instanceId={}.",
                        recipientInstanceId, instanceInfoManager.getInstanceId());
            }
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage(), e);
        }
    }

}
