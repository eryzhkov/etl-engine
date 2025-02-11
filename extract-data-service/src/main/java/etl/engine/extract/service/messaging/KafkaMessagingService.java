package etl.engine.extract.service.messaging;

import etl.engine.extract.model.messaging.EmsMessage;
import etl.engine.extract.model.messaging.EmsMessageExecutionNotificationPayload;
import etl.engine.extract.model.messaging.EmsMessageInfo;
import etl.engine.extract.model.messaging.EmsMessageInstanceStatusPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaMessagingService implements MessagingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${eds.kafka.topics.heartbeat}")
    private String heartBeatTopicName;

    @Value("${eds.kafka.topics.progress}")
    private String progressTopicName;

    @Override
    @Transactional("kafkaTransactionManager")
    public void publishInstanceStatus(EmsMessage<EmsMessageInfo, EmsMessageInstanceStatusPayload> message) {
        kafkaTemplate.send(heartBeatTopicName, message);
    }

    @Override
    @Transactional("kafkaTransactionManager")
    public void publishEtlExecutionNotification(String notificationType, UUID etlExecutionId) {
        final EmsMessage<EmsMessageInfo, EmsMessageExecutionNotificationPayload> notificationMessage =
                new EmsMessage<>(
                        new EmsMessageInfo(notificationType),
                        new EmsMessageExecutionNotificationPayload(etlExecutionId, null)
                );
        kafkaTemplate.send(progressTopicName, notificationMessage);
    }

    @Override
    @Transactional("kafkaTransactionManager")
    public void publishEtlExecutionNotificationWithMessage(String notificationType, UUID etlExecutionId,
            String message) {
        final EmsMessage<EmsMessageInfo, EmsMessageExecutionNotificationPayload> notificationMessage =
                new EmsMessage<>(
                        new EmsMessageInfo(notificationType),
                        new EmsMessageExecutionNotificationPayload(etlExecutionId, message)
                );
        kafkaTemplate.send(progressTopicName, notificationMessage);
    }
}
