package etl.engine.worker.service.messaging;

import etl.engine.worker.model.messaging.EmsMessage;
import etl.engine.worker.model.messaging.Info;
import etl.engine.worker.model.messaging.InstanceInfoPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional("kafkaTransactionManager")
public class KafkaMessagingService implements MessagingService {

    @Value("${ews.kafka.topics.heartbeat}")
    private String heartBeatTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishInstanceInfo(EmsMessage<Info, InstanceInfoPayload> message) {
        kafkaTemplate.send(heartBeatTopicName, message);
    }
}
