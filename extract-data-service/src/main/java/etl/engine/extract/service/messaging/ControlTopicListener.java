package etl.engine.extract.service.messaging;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.extract.service.instance.InstanceStatus;
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

    private final static String INFO_RECIPIENT_PTR = "/info/recipientInstanceId";

    private final InstanceStatus instanceStatus;
    private final ObjectMapper mapper;

    @KafkaHandler
    @Transactional
    public void listener(String message) {
        log.debug("Got a command: {}", message);
        try {
            JsonNode document = mapper.readTree(message);
            JsonPointer recipientInstanceIdPtr = JsonPointer.compile(INFO_RECIPIENT_PTR);
            final String recipientInstanceId = document.at(recipientInstanceIdPtr).asText();
            log.debug("recipientInstanceId = '{}'", recipientInstanceId);
            log.debug("instanceId = '{}'", instanceStatus.getInstanceId());
            if (instanceStatus.getInstanceId().toString().equals(recipientInstanceId)) {
                log.info("The received command should be processed in the instance.");
            } else {
                log.info("The received command is not for the instance and ignored.");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
