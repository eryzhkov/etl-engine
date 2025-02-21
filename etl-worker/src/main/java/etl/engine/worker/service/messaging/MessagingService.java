package etl.engine.worker.service.messaging;

import etl.engine.worker.model.messaging.EmsMessage;
import etl.engine.worker.model.messaging.Info;
import etl.engine.worker.model.messaging.InstanceInfoPayload;

public interface MessagingService {

    void publishInstanceInfo(EmsMessage<Info, InstanceInfoPayload> message);

}
