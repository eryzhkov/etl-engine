package etl.engine.ems.service;

import etl.engine.ems.dao.entity.EtlExecution;
import etl.engine.ems.dao.entity.EtlPhase;
import etl.engine.ems.dao.entity.EtlStreamExecution;
import etl.engine.ems.dao.repository.EtlExecutionRepository;
import etl.engine.ems.dao.repository.EtlPhaseRepository;
import etl.engine.ems.dao.repository.EtlStreamExecutionRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlDataStreamExecutionService {

    private final EtlPhaseRepository etlPhaseRepository;
    private final EtlExecutionRepository etlExecutionRepository;
    private final EtlStreamExecutionRepository etlStreamExecutionRepository;

    @Transactional(propagation = Propagation.NESTED)
    public void startEtlDataStream(UUID etlExecutionId, String phase, String dataStreamName, UUID instanceId, OffsetDateTime timestamp)
            throws EntityNotFoundException {

        log.debug("Try if an ETL-stream-execution already exists for etlExecutionId={}, phase={}, dataStreamName={}...",
                etlExecutionId, phase, dataStreamName);

        Optional<EtlStreamExecution> etlStreamExecutionOpt = etlStreamExecutionRepository
                .findEtlStreamExecutionByEtlExecution_IdAndEtlPhase_CodeAndStreamName(etlExecutionId, phase, dataStreamName);

        if (etlStreamExecutionOpt.isEmpty()) {
            log.debug("The ETL-stream-execution was not found.");
            EtlExecution etlExecution = etlExecutionRepository
                    .findById(etlExecutionId)
                    .orElseThrow(() -> new EntityNotFoundException("Start data stream '" + dataStreamName + "' is failed: no ETL-execution with id='" + etlExecutionId + "' was found."));
            log.debug("Found the parent ETL-execution: {}", etlExecution);

            EtlPhase etlPhase = etlPhaseRepository
                    .findEtlPhaseByCode(phase)
                    .orElseThrow(() -> new EntityNotFoundException("Start data stream '" + dataStreamName + "' is failed: no ETL-phase with code='" + phase + "' was found."));
            log.debug("Found the parent ETL-phase: {}", etlPhase);

            EtlStreamExecution etlStreamExecution = new EtlStreamExecution();
            etlStreamExecution.setEtlExecution(etlExecution);
            etlStreamExecution.setEtlPhase(etlPhase);
            etlStreamExecution.setStreamName(dataStreamName);
            etlStreamExecution.setServiceInstanceId(instanceId);
            etlStreamExecution.setStreamStartedAt(timestamp);
            log.debug("The new ETL-stream-execution is ready to be saved: {}", etlStreamExecution);
            etlStreamExecutionRepository.save(etlStreamExecution);
            log.debug("Data stream '{}' for the etlExecution='{}' in the phase='{}' was created.", dataStreamName, etlExecutionId, phase);
        } else {
            log.warn("The data stream with dataStreamName='{}', etlExecutionId='{}' and phase='{}' is already started. Nothing to do",
                    dataStreamName, etlExecutionId, phase);
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void finishEtlDataStream(UUID etlExecutionId, String phase, String dataStreamName, OffsetDateTime timestamp)
            throws EntityNotFoundException {
        EtlStreamExecution etlStreamExecution = etlStreamExecutionRepository
                .findEtlStreamExecutionByEtlExecution_IdAndEtlPhase_CodeAndStreamName(etlExecutionId, phase,
                        dataStreamName)
                .orElseThrow(() -> new EntityNotFoundException("Data stream '" + dataStreamName + "' for the executionId='" + etlExecutionId + "' in the phase='" + phase + "' was not found."));
        etlStreamExecution.setStreamFinishedAt(timestamp);
        etlStreamExecutionRepository.save(etlStreamExecution);
        log.debug("Data stream '{}' for the etlExecution='{}' in the phase='{}' was finished.", dataStreamName,
                etlExecutionId, phase);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void failEtlDataStream(UUID etlExecutionId, String phase, String dataStreamName, OffsetDateTime timestamp, String error)
            throws EntityNotFoundException {
        EtlStreamExecution etlStreamExecution = etlStreamExecutionRepository
                .findEtlStreamExecutionByEtlExecution_IdAndEtlPhase_CodeAndStreamName(etlExecutionId, phase,
                        dataStreamName)
                .orElseThrow(() -> new EntityNotFoundException("Data stream '" + dataStreamName + "' for the executionId='" + etlExecutionId + "' in the phase='" + phase + "' was not found."));
        etlStreamExecution.setStreamFailedAt(timestamp);
        etlStreamExecution.setComment(error);
        etlStreamExecutionRepository.save(etlStreamExecution);
        log.debug("Data stream '{}' for the etlExecution='{}' in the phase='{}' was failed with the error '{}'.", dataStreamName,
                etlExecutionId, phase, error);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void updateEtlDataStreamStats(
            UUID etlExecutionId,
            String phase,
            String dataStreamName,
            long totalInMessages,
            long totalOutMessages,
            long totalFailedMessages) throws EntityNotFoundException {
        EtlStreamExecution etlStreamExecution = etlStreamExecutionRepository
                .findEtlStreamExecutionByEtlExecution_IdAndEtlPhase_CodeAndStreamName(etlExecutionId, phase,
                        dataStreamName)
                .orElseThrow(() -> new EntityNotFoundException("Data stream '" + dataStreamName + "' for the executionId='" + etlExecutionId + "' in the phase='" + phase + "' was not found."));
        etlStreamExecution.setTotalInMessages(totalInMessages);
        etlStreamExecution.setTotalOutMessages(totalOutMessages);
        etlStreamExecution.setTotalFailedMessages(totalFailedMessages);
        etlStreamExecutionRepository.save(etlStreamExecution);
        log.debug("Data stream '{}' for the etlExecution='{}' in the phase='{}' was updated with totalInMessages='{}', totalOutMessages='{}', totalFailedMessages='{}'.",
                dataStreamName, etlExecutionId, phase, totalInMessages, totalOutMessages, totalFailedMessages);
    }
}
