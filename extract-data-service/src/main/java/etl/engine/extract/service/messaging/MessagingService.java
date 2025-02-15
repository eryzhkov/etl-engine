package etl.engine.extract.service.messaging;

import etl.engine.extract.model.messaging.EmsMessage;
import etl.engine.extract.model.messaging.EmsMessageDataStreamStatsPayload;
import etl.engine.extract.model.messaging.EmsMessageInfo;
import etl.engine.extract.model.messaging.EmsMessageInstanceStatusPayload;

import java.util.UUID;

public interface MessagingService {

    void publishInstanceStatus(EmsMessage<EmsMessageInfo, EmsMessageInstanceStatusPayload> message);
    void publishEtlExecutionNotification(String notificationType, UUID etlExecutionId);
    void publishEtlExecutionNotificationWithMessage(String notificationType, UUID etlExecutionId, String message);
    void publishEtlDataStreamNotification(String notificationType, UUID etlExecutionId, String dataStreamName);
    void publishEtlDataStreamNotificationWithMessage(String notificationType, UUID etlExecutionId, String dataStreamName, String message);
    void publishEtlDataStreamStats(String notificationType, EmsMessageDataStreamStatsPayload payload);

}
