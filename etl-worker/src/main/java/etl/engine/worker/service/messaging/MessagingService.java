package etl.engine.worker.service.messaging;

import etl.engine.worker.model.messaging.EmsMessage;
import etl.engine.worker.model.messaging.Info;
import etl.engine.worker.model.messaging.InstanceInfoPayload;
import etl.engine.worker.model.messaging.ProgressPayload;

public interface MessagingService {

    void publishInstanceInfo(EmsMessage<Info, InstanceInfoPayload> message);

    void acceptEtlExecution(EmsMessage<Info, ProgressPayload> message);

}
