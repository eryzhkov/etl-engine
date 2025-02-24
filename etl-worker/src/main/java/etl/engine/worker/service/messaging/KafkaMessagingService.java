package etl.engine.worker.service.messaging;

import etl.engine.worker.model.messaging.EmsMessage;
import etl.engine.worker.model.messaging.Info;
import etl.engine.worker.model.messaging.InstanceInfoPayload;
import etl.engine.worker.model.messaging.ProgressPayload;
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

    @Value("${ews.kafka.topics.heartbeat}")
    private String heartBeatTopicName;

    @Value("${ews.kafka.topics.progress}")
    private String progressTopicName;

    @Value("#{instanceInfoManager.instanceId}")
    private UUID INSTANCE_ID;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishInstanceInfo(EmsMessage<Info, InstanceInfoPayload> message) {
        kafkaTemplate.send(heartBeatTopicName, message);
    }

    @Override
    public void acceptEtlExecution(UUID etlExecutionId) {
        EmsMessage<Info, ProgressPayload> message = new EmsMessage<>(
                new Info(Info.ETL_EXECUTION_ACCEPTED),
                ProgressPayload.builder()
                        .assignee(INSTANCE_ID)
                        .etlExecutionId(etlExecutionId)
                        .build()
        );
        kafkaTemplate.send(progressTopicName, message);
    }

    @Override
    public void rejectEtlExecution(UUID etlExecutionId) {
        EmsMessage<Info, ProgressPayload> message = new EmsMessage<>(
                new Info(Info.ETL_EXECUTION_REJECTED),
                ProgressPayload.builder()
                        .assignee(INSTANCE_ID)
                        .etlExecutionId(etlExecutionId)
                        .build()
        );
        kafkaTemplate.send(progressTopicName, message);
    }

    @Override
    public void startEtlExecution(UUID etlExecutionId) {
        EmsMessage<Info, ProgressPayload> message = new EmsMessage<>(
                new Info(Info.ETL_EXECUTION_STARTED),
                ProgressPayload.builder()
                        .assignee(INSTANCE_ID)
                        .etlExecutionId(etlExecutionId)
                        .build()
        );
        kafkaTemplate.send(progressTopicName, message);
    }

    @Override
    public void finishEtlExecution(UUID etlExecutionId) {
        EmsMessage<Info, ProgressPayload> message = new EmsMessage<>(
                new Info(Info.ETL_EXECUTION_FINISHED),
                ProgressPayload.builder()
                        .assignee(INSTANCE_ID)
                        .etlExecutionId(etlExecutionId)
                        .build()
        );
        kafkaTemplate.send(progressTopicName, message);
    }
}
