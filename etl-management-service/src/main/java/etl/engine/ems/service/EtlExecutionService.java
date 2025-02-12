package etl.engine.ems.service;

import etl.engine.ems.dao.entity.EtlExecution;
import etl.engine.ems.dao.entity.EtlExecutionStatus;
import etl.engine.ems.dao.repository.EtlExecutionRepository;
import etl.engine.ems.dao.repository.EtlExecutionStatusRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionService {

    private final EtlExecutionRepository etlExecutionRepository;
    private final EtlExecutionStatusRepository etlExecutionStatusRepository;


    @Transactional(propagation = Propagation.NESTED)
    public void markEtlExecutionAsAcceptedAt(UUID etlExecutionId, OffsetDateTime acceptedAt)
            throws EntityNotFoundException {
        log.debug("etlExecutionId = {}, acceptedAt = {}", etlExecutionId, acceptedAt);
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> new EntityNotFoundException("ETL-execution with id=" + etlExecutionId + " not found."));
        log.debug("Found {}", etlExecution);
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.ACCEPTED)
                .orElseThrow(() -> new EntityNotFoundException("The required ETL execution status 'ACCEPTED' was not found in the repository!"));
        log.debug("The status 'ACCEPTED' was found in the repository");
        etlExecution.setStatus(status);
        etlExecution.setAcceptedAt(acceptedAt);
        log.debug("Updated the acceptedAt property.");
        etlExecutionRepository.save(etlExecution);
        log.debug("Saved the entity");
    }

    @Transactional(propagation = Propagation.NESTED)
    public void markEtlExecutionAsStartedAt(UUID etlExecutionId, OffsetDateTime startedAt)
            throws EntityNotFoundException {
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> new EntityNotFoundException("ETL-execution with id=" + etlExecutionId + " not found."));
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.RUNNING)
                .orElseThrow(() -> new EntityNotFoundException("The required ETL execution status 'RUNNING' was not found in the repository!"));
        log.debug("The status 'RUNNING' was found in the repository");
        etlExecution.setStatus(status);
        etlExecution.setStartedAt(startedAt);
        etlExecutionRepository.save(etlExecution);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void markEtlExecutionAsFinishedAt(UUID etlExecutionId, OffsetDateTime finishedAt)
            throws EntityNotFoundException {
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> new EntityNotFoundException("ETL-execution with id=" + etlExecutionId + " not found."));
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.FINISHED)
                .orElseThrow(() -> new EntityNotFoundException("The required ETL execution status 'FINISHED' was not found in the repository!"));
        log.debug("The status 'FINISHED' was found in the repository");
        etlExecution.setStatus(status);
        etlExecution.setFinishedAt(finishedAt);
        etlExecutionRepository.save(etlExecution);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void markEtlExecutionAsFailedAt(UUID etlExecutionId, OffsetDateTime failedAt, String reason)
            throws EntityNotFoundException {
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> new EntityNotFoundException("ETL-execution with id=" + etlExecutionId + " not found."));
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.FAILED)
                .orElseThrow(() -> new EntityNotFoundException("The required ETL execution status 'FAILED' was not found in the repository!"));
        log.debug("The status 'FAILED' was found in the repository");
        etlExecution.setStatus(status);
        etlExecution.setFinishedAt(failedAt);
        etlExecution.setComment(reason);
        etlExecutionRepository.save(etlExecution);
    }

}
