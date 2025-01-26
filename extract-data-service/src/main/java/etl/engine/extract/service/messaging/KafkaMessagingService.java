package etl.engine.extract.service.messaging;

import etl.engine.extract.model.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessagingService implements MessagingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${eds.topics.heartbeat}")
    private String heartBeatTopicName;

    @Override
    public <T> void sendInstanceStatusReport(Event<T> instanceStatusReport) {
        kafkaTemplate.send(heartBeatTopicName, instanceStatusReport);
    }
}
