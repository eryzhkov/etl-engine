package etl.engine.worker.service.messaging;

import etl.engine.worker.model.messaging.EmsMessage;
import etl.engine.worker.model.messaging.Info;
import etl.engine.worker.model.messaging.InstanceInfoPayload;

import java.util.UUID;

public interface MessagingService {

    void publishInstanceInfo(EmsMessage<Info, InstanceInfoPayload> message);

    void acceptEtlExecution(UUID etlExecutionId);
    void rejectEtlExecution(UUID etlExecutionId);
    void startEtlExecution(UUID etlExecutionId);
    void finishEtlExecution(UUID etlExecutionId);

}
