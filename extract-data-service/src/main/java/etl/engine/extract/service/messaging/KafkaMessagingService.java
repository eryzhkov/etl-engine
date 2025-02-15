package etl.engine.extract.service.messaging;

import etl.engine.extract.model.messaging.EmsMessage;
import etl.engine.extract.model.messaging.EmsMessageDataStreamPayload;
import etl.engine.extract.model.messaging.EmsMessageDataStreamStatsPayload;
import etl.engine.extract.model.messaging.EmsMessageExecutionNotificationPayload;
import etl.engine.extract.model.messaging.EmsMessageInfo;
import etl.engine.extract.model.messaging.EmsMessageInstanceStatusPayload;
import etl.engine.extract.service.instance.InstanceInfoManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional("kafkaTransactionManager")
public class KafkaMessagingService implements MessagingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final InstanceInfoManager instanceInfoManager;

    @Value("${eds.kafka.topics.heartbeat}")
    private String heartBeatTopicName;

    @Value("${eds.kafka.topics.progress}")
    private String progressTopicName;

    @Override
    public void publishInstanceStatus(EmsMessage<EmsMessageInfo, EmsMessageInstanceStatusPayload> message) {
        kafkaTemplate.send(heartBeatTopicName, message);
    }

    @Override
    public void publishEtlExecutionNotification(String notificationType, UUID etlExecutionId) {
        final EmsMessage<EmsMessageInfo, EmsMessageExecutionNotificationPayload> notificationMessage =
                new EmsMessage<>(
                        new EmsMessageInfo(notificationType),
                        new EmsMessageExecutionNotificationPayload(etlExecutionId, null)
                );
        kafkaTemplate.send(progressTopicName, notificationMessage);
    }

    @Override
    public void publishEtlExecutionNotificationWithMessage(String notificationType, UUID etlExecutionId,
            String message) {
        final EmsMessage<EmsMessageInfo, EmsMessageExecutionNotificationPayload> notificationMessage =
                new EmsMessage<>(
                        new EmsMessageInfo(notificationType),
                        new EmsMessageExecutionNotificationPayload(etlExecutionId, message)
                );
        kafkaTemplate.send(progressTopicName, notificationMessage);
    }

    @Override
    public void publishEtlDataStreamNotification(String notificationType, UUID etlExecutionId, String dataStreamName) {
        final EmsMessage<EmsMessageInfo, EmsMessageDataStreamPayload> notificationMessage =
                new EmsMessage<>(
                        new EmsMessageInfo(notificationType),
                        new EmsMessageDataStreamPayload(
                                etlExecutionId,
                                instanceInfoManager.getPhase(),
                                dataStreamName,
                                instanceInfoManager.getInstanceId(),
                                null)
                );
        kafkaTemplate.send(progressTopicName, notificationMessage);
    }

    @Override
    public void publishEtlDataStreamNotificationWithMessage(String notificationType, UUID etlExecutionId, String dataStreamName,
            String message) {
        final EmsMessage<EmsMessageInfo, EmsMessageDataStreamPayload> notificationMessage =
                new EmsMessage<>(
                        new EmsMessageInfo(notificationType),
                        new EmsMessageDataStreamPayload(
                                etlExecutionId,
                                instanceInfoManager.getPhase(),
                                dataStreamName,
                                instanceInfoManager.getInstanceId(),
                                message)
                );
        kafkaTemplate.send(progressTopicName, notificationMessage);
    }

    @Override
    public void publishEtlDataStreamStats(String notificationType, EmsMessageDataStreamStatsPayload payload) {
        final EmsMessage<EmsMessageInfo, EmsMessageDataStreamStatsPayload> notificationMessage =
                new EmsMessage<>(
                  new EmsMessageInfo(notificationType),
                  payload
                );
        kafkaTemplate.send(progressTopicName, notificationMessage);
    }
}
