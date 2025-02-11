package etl.engine.extract.service;

import com.fasterxml.jackson.core.JsonPointer;
import etl.engine.extract.exception.EtlConfigurationLoadException;
import etl.engine.extract.exception.EtlConfigurationParseException;
import etl.engine.extract.model.EtlExecutionInfo;
import etl.engine.extract.model.messaging.EmsMessageEtlExecutionStartPayload;
import etl.engine.extract.model.messaging.EmsMessageInfo;
import etl.engine.extract.service.instance.InstanceInfoManager;
import etl.engine.extract.service.messaging.MessagingService;
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
    private final MessagingService messagingService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async("etlExecutionPool")
    public void runEtlExecution(EmsMessageEtlExecutionStartPayload commandPayload) {
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
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_ACCEPTED_TYPE,
                    etlExecutionInfo.getExecutionId());

            // Create the extractor
            String extractorType = etlExecutionInfo.getConfiguration().at(JsonPointer.compile("/extractor/type")).asText();
            log.debug("Found extractor type is '{}'.", extractorType);

            // Notify EMS the ETL-execution is started.
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_STARTED_TYPE,
                    etlExecutionInfo.getExecutionId());

            // Notify EMS the ETL-execution is finished.
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_FINISHED_TYPE,
                    etlExecutionInfo.getExecutionId());

            instanceInfoManager.deleteEtlExecutionInfo(etlExecutionInfo.getExecutionId());
            log.debug("ETL-execution info is deleted from the instance info.");
            //TODO Send the 'finished' message to the sts.data topics!
        } catch (EtlConfigurationLoadException | EtlConfigurationParseException e) {
            log.error("{}", e.getMessage(), e);
            // Notify EMS the ETL-execution is failed.
            messagingService.publishEtlExecutionNotificationWithMessage(
                    EmsMessageInfo.ETL_EXECUTION_FAILED_TYPE,
                    commandPayload.getEtlExecutionId(),
                    e.getMessage()
            );
        }
    }
}
