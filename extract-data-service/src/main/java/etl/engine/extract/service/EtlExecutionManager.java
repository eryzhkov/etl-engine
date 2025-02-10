package etl.engine.extract.service;

import com.fasterxml.jackson.core.JsonPointer;
import etl.engine.extract.exception.EtlConfigurationLoadException;
import etl.engine.extract.exception.EtlConfigurationParseException;
import etl.engine.extract.model.EtlExecutionInfo;
import etl.engine.extract.service.instance.InstanceInfoManager;
import etl.engine.extract.service.messaging.model.EtlExecutionPayload;
import etl.engine.extract.service.messaging.model.EtlExecutionStartCommandPayload;
import etl.engine.extract.service.messaging.model.EtlNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionManager {

    @Value("${eds.kafka.topics.progress}")
    private String progressTopicName;

    private final InstanceInfoManager instanceInfoManager;
    private final EtlExecutionInfoProvider etlExecutionInfoProvider;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async("etlExecutionPool")
    public void runEtlExecution(EtlExecutionStartCommandPayload commandPayload) {
        log.debug("Start ETL-execution for the command {}", commandPayload);
        try {
            EtlExecutionInfo etlExecutionInfo = etlExecutionInfoProvider.getExecutionInfo(
                    commandPayload.getExternalSystemCode(),
                    commandPayload.getEtlProcessCode(),
                    commandPayload.getEtlExecutionId()
            );
            instanceInfoManager.addEtlExecutionInfo(etlExecutionInfo);
            log.debug("ETL-execution info is added to the instance info.");

            // Notify EMS the ETL-execution is accepted.
            final EtlNotification<EtlExecutionPayload> acceptedNotification = new EtlNotification<>(
                    EtlNotification.ETL_EXECUTION_ACCEPTED,
                    new EtlExecutionPayload(etlExecutionInfo.getExecutionId()));
            kafkaTemplate.executeInTransaction(
                    template -> template.send(progressTopicName, acceptedNotification)
            );

            // Create the extractor
            String extractorType = etlExecutionInfo.getConfiguration().at(JsonPointer.compile("/extractor/type")).asText();
            log.debug("Found extractor type is '{}'.", extractorType);

            // Notify EMS the ETL-execution is started.
            final EtlNotification<EtlExecutionPayload> startedNotification = new EtlNotification<>(
                    EtlNotification.ETL_EXECUTION_STARTED,
                    new EtlExecutionPayload(etlExecutionInfo.getExecutionId()));
            kafkaTemplate.executeInTransaction(
                    template -> template.send(progressTopicName, startedNotification)
            );

            // Notify EMS the ETL-execution is finished.
            final EtlNotification<EtlExecutionPayload> finishedNotification = new EtlNotification<>(
                    EtlNotification.ETL_EXECUTION_FINISHED,
                    new EtlExecutionPayload(etlExecutionInfo.getExecutionId()));
            kafkaTemplate.executeInTransaction(
                    template -> template.send(progressTopicName, finishedNotification)
            );

            instanceInfoManager.deleteEtlExecutionInfo(etlExecutionInfo.getExecutionId());
            log.debug("ETL-execution info is deleted from the instance info.");
            //TODO Send the 'finished' message to the ems.progress/sts.data topics!
        } catch (EtlConfigurationLoadException | EtlConfigurationParseException e) {
            log.error("{}", e.getMessage(), e);
            // Notify EMS the ETL-execution is failed.
            final EtlNotification<String> failedNotification = new EtlNotification<>(
                    EtlNotification.ETL_EXECUTION_FAILED,
                    e.getMessage());
            kafkaTemplate.executeInTransaction(
                    template -> template.send(progressTopicName, failedNotification)
            );
        }
    }
}
