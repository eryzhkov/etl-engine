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
            EtlExecutionInfo etlExecutionInfo = etlExecutionInfoProvider.getExecutionInfo(
                    commandPayload.getExternalSystemCode(),
                    commandPayload.getEtlProcessCode(),
                    commandPayload.getEtlExecutionId()
            );
            instanceInfoManager.addEtlExecutionInfo(etlExecutionInfo);
            log.debug("ETL-execution info is added to the instance info.");

            // Notify EMS the ETL-execution is accepted.
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_ACCEPTED,
                    etlExecutionInfo.getExecutionId());

            // Create the extractor
            String extractorType = etlExecutionInfo.getConfiguration().at(JsonPointer.compile("/extractor/type")).asText();
            log.debug("Found extractor type is '{}'.", extractorType);

            // Notify EMS the ETL-execution is started.
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_STARTED,
                    etlExecutionInfo.getExecutionId());

            EtlDataExtractor etlDataExtractor = etlExtractorFactory.createExtractor(
                    etlExecutionInfo.getConfiguration().at(JsonPointer.compile("/extractor"))
            );

            // Iterate over all data streams
            Collection<DataStreamInfo> dataStreamInfos = etlExecutionInfo.getDataStreamsInfo().values();
            for (DataStreamInfo dataStreamInfo : dataStreamInfos) {
                try {
                    messagingService.publishEtlDataStreamNotification(
                            EmsMessageInfo.ETL_DATA_STREAM_STARTED,
                            etlExecutionInfo.getExecutionId(),
                            dataStreamInfo.getDataStreamName()
                    );
                    EtlStreamData etlStreamData = etlDataExtractor.extractData(dataStreamInfo.getConfig());

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
                    messagingService.publishEtlDataStreamNotificationWithMessage(
                            EmsMessageInfo.ETL_DATA_STREAM_FAILED,
                            etlExecutionInfo.getExecutionId(),
                            dataStreamInfo.getDataStreamName(),
                            e.getMessage()
                    );
                }
            }

            // Notify EMS the ETL-execution is finished.
            messagingService.publishEtlExecutionNotification(
                    EmsMessageInfo.ETL_EXECUTION_FINISHED,
                    etlExecutionInfo.getExecutionId());

            instanceInfoManager.deleteEtlExecutionInfo(etlExecutionInfo.getExecutionId());
            log.debug("ETL-execution info is deleted from the instance info.");
            //TODO Send the 'finished' message to the sts.data topics!
        } catch (EtlConfigurationLoadException | EtlConfigurationParseException | EtlUnknownExtractorTypeException e) {
            log.error("{}", e.getMessage(), e);
            messagingService.publishEtlExecutionNotificationWithMessage(
                    EmsMessageInfo.ETL_EXECUTION_FAILED,
                    commandPayload.getEtlExecutionId(),
                    e.getMessage()
            );
        }
    }
}
