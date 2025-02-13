package etl.engine.extract.service;

import com.fasterxml.jackson.core.JsonPointer;
import etl.engine.extract.exception.EtlConfigurationLoadException;
import etl.engine.extract.exception.EtlConfigurationParseException;
import etl.engine.extract.exception.EtlExtractDataException;
import etl.engine.extract.exception.EtlUnknownExtractorTypeException;
import etl.engine.extract.model.DataStreamInfo;
import etl.engine.extract.model.EtlExecutionInfo;
import etl.engine.extract.model.EtlStreamData;
import etl.engine.extract.model.messaging.EmsMessageDataStreamStatsPayload;
import etl.engine.extract.model.messaging.EmsMessageEtlExecutionStartPayload;
import etl.engine.extract.model.messaging.EmsMessageInfo;
import etl.engine.extract.service.extractor.EtlDataExtractor;
import etl.engine.extract.service.extractor.EtlExtractorFactory;
import etl.engine.extract.service.instance.InstanceInfoManager;
import etl.engine.extract.service.messaging.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionManager {

    @Value("${eds.kafka.topics.progress}")
    private String progressTopicName;

    private final InstanceInfoManager instanceInfoManager;
    private final EtlExecutionInfoProvider etlExecutionInfoProvider;
    private final MessagingService messagingService;
    private final EtlExtractorFactory etlExtractorFactory;

    @Async("etlExecutionPool")
    public void runEtlExecution(EmsMessageEtlExecutionStartPayload commandPayload) {
        log.debug("Start ETL-execution for the command {}", commandPayload);
        try {
            // Read ETL-configuration and prepare the execution info.
            EtlExecutionInfo etlExecutionInfo = etlExecutionInfoProvider.getExecutionInfo(
                    commandPayload.getExternalSystemCode(),
                    commandPayload.getEtlProcessCode(),
                    commandPayload.getEtlExecutionId()
            );
            // Add the execution info to the instance info.
            // It makes the execution info available via Actuator and includes the info to the regular instance status.
            instanceInfoManager.addEtlExecutionInfo(etlExecutionInfo);
            log.debug("ETL-execution info is added to the instance info.");

            // Notify EMS the ETL-execution is accepted.
            // Acceptance means we've got all information to run the ETL-execution.
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_ACCEPTED,
                    etlExecutionInfo.getExecutionId());

            // It looks strange to send two notifications instead of just one (ACCEPTED and STARTED).
            // I stick to the approach until I don't decide will it be possible to accept ETL-execution
            // while the previous one is still running. For instance, there could be a requirement to run only one ETL-execution
            // at once but all new ones should be kept in a queue. Or we can decide to have an ability to run several
            // ETL-executions in parallel. It's still an open question for me with low priority.

            // Notify EMS the ETL-execution is started.
            // It should be sent before the EtlDataExtractor instance is created!
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_STARTED,
                    etlExecutionInfo.getExecutionId());

            try (
                    // EtlDataExtractor use the specific logic to connect to the external datasource.
                    // Initialising the extractor could result in error due to impossibility to connect to the external datasource.
                    EtlDataExtractor etlDataExtractor = etlExtractorFactory.createExtractor(
                            etlExecutionInfo.getConfiguration().at(JsonPointer.compile("/extractor")));
                ) {

                // Iterate over all data streams in the ETL-configuration
                Collection<DataStreamInfo> dataStreamInfos = etlExecutionInfo.getDataStreamsInfo().values();
                for (DataStreamInfo dataStreamInfo : dataStreamInfos) {
                    try {
                        messagingService.publishEtlDataStreamNotification(
                                EmsMessageInfo.ETL_DATA_STREAM_STARTED,
                                etlExecutionInfo.getExecutionId(),
                                dataStreamInfo.getDataStreamName()
                        );
                        EtlStreamData etlStreamData = etlDataExtractor.extractData(dataStreamInfo.getConfig());

                        // Send data stream's stats information to EMS.
                        EmsMessageDataStreamStatsPayload streamStatsPayload = new EmsMessageDataStreamStatsPayload(
                                etlExecutionInfo.getExecutionId(),
                                instanceInfoManager.getPhase(),
                                dataStreamInfo.getDataStreamName(),
                                instanceInfoManager.getInstanceId(),
                                null,
                                etlStreamData.getTotalIn(),
                                0,
                                etlStreamData.getTotalFailed()
                        );
                        messagingService.publishEtlDataStreamStats(EmsMessageInfo.ETL_DATA_STREAM_STATS, streamStatsPayload);

                        messagingService.publishEtlDataStreamNotification(
                                EmsMessageInfo.ETL_DATA_STREAM_FINISHED,
                                etlExecutionInfo.getExecutionId(),
                                dataStreamInfo.getDataStreamName()
                        );
                    } catch (EtlExtractDataException e) {
                        log.error("{}", e.getMessage(), e);
                        //TODO How the next service should be notified (sts.data topic) about the failure?
                        messagingService.publishEtlDataStreamNotificationWithMessage(
                                EmsMessageInfo.ETL_DATA_STREAM_FAILED,
                                etlExecutionInfo.getExecutionId(),
                                dataStreamInfo.getDataStreamName(),
                                e.getMessage()
                        );
                    }
                }

                // Notify EMS the ETL-execution is finished.
                //TODO Send the 'finished' message to the sts.data topics too!
                messagingService.publishEtlExecutionNotification(
                        EmsMessageInfo.ETL_EXECUTION_FINISHED,
                        etlExecutionInfo.getExecutionId());

                instanceInfoManager.deleteEtlExecutionInfo(etlExecutionInfo.getExecutionId());
                log.debug("ETL-execution info is deleted from the instance info.");

            } catch (EtlUnknownExtractorTypeException e) {
                log.error("{}", e.getMessage(), e);
                messagingService.publishEtlExecutionNotificationWithMessage(
                        EmsMessageInfo.ETL_EXECUTION_FAILED,
                        commandPayload.getEtlExecutionId(),
                        e.getMessage()
                );
            } catch (Exception e) {
                // Could be thrown by AutoCloseable implementations!
                // Technically, Apache Kafka exceptions can be caught here too.
                //TODO AutoCloseable exceptions are OK - all data streams are processed. What about Kafka errors?
                log.error("{}", e.getMessage(), e);
            }

        } catch (EtlConfigurationLoadException | EtlConfigurationParseException e) {
            log.error("{}", e.getMessage(), e);
            messagingService.publishEtlExecutionNotificationWithMessage(
                    EmsMessageInfo.ETL_EXECUTION_FAILED,
                    commandPayload.getEtlExecutionId(),
                    e.getMessage()
            );
        }
    }
}
