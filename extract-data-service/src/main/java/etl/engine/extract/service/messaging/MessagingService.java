package etl.engine.extract.service.messaging;

import etl.engine.extract.model.messaging.EmsMessage;
import etl.engine.extract.model.messaging.EmsMessageInfo;
import etl.engine.extract.model.messaging.EmsMessageInstanceStatusPayload;

import java.util.UUID;

public interface MessagingService {

    void publishInstanceStatus(EmsMessage<EmsMessageInfo, EmsMessageInstanceStatusPayload> message);
    void publishEtlExecutionNotification(String notificationType, UUID etlExecutionId);
    void publishEtlExecutionNotificationWithMessage(String notificationType, UUID etlExecutionId, String message);

}
