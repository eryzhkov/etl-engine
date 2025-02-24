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
    public void markEtlExecutionAsAcceptedAt(UUID etlExecutionId, OffsetDateTime acceptedAt, UUID assignee)
            throws EntityNotFoundException {
        log.debug("etlExecutionId = {}, acceptedAt = {}", etlExecutionId, acceptedAt);
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecution(etlExecutionId));
        log.debug("Found {}", etlExecution);
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.ACCEPTED)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecutionStatus(EtlExecutionStatus.ACCEPTED));
        log.debug("The status '{}' was found in the repository", EtlExecutionStatus.ACCEPTED);
        etlExecution.setStatus(status);
        etlExecution.setAcceptedAt(acceptedAt);
        etlExecution.setEtlWorker(assignee);
        log.debug("Updated the acceptedAt property.");
        etlExecutionRepository.save(etlExecution);
        log.debug("The entity is saved");
    }

    public void markEtlExecutionAsRejected(UUID etlExecutionId, OffsetDateTime rejectedAt)
            throws EntityNotFoundException {
        log.debug("etlExecutionId = {}, rejectedAt = {}", etlExecutionId, rejectedAt);
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecution(etlExecutionId));
        log.debug("Found {}", etlExecution);
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.REJECTED)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecutionStatus(EtlExecutionStatus.REJECTED));
        log.debug("The status '{}' was found in the repository", EtlExecutionStatus.REJECTED);
        etlExecution.setStatus(status);
        etlExecution.setFinishedAt(rejectedAt);
        log.debug("Updated the finishedAt property.");
        etlExecutionRepository.save(etlExecution);
        log.debug("The entity is saved");
    }

    @Transactional(propagation = Propagation.NESTED)
    public void markEtlExecutionAsStartedAt(UUID etlExecutionId, OffsetDateTime startedAt)
            throws EntityNotFoundException {
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecution(etlExecutionId));
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.RUNNING)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecutionStatus(EtlExecutionStatus.RUNNING));
        log.debug("The status '{}' was found in the repository", EtlExecutionStatus.RUNNING);
        etlExecution.setStatus(status);
        etlExecution.setStartedAt(startedAt);
        etlExecutionRepository.save(etlExecution);
        log.debug("The entity is saved");
    }

    @Transactional(propagation = Propagation.NESTED)
    public void markEtlExecutionAsFinishedAt(UUID etlExecutionId, OffsetDateTime finishedAt)
            throws EntityNotFoundException {
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecution(etlExecutionId));
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.FINISHED)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecutionStatus(EtlExecutionStatus.FINISHED));
        log.debug("The status '{}' was found in the repository", EtlExecutionStatus.FINISHED);
        etlExecution.setStatus(status);
        etlExecution.setFinishedAt(finishedAt);
        etlExecutionRepository.save(etlExecution);
        log.debug("The entity is saved");
    }

    @Transactional(propagation = Propagation.NESTED)
    public void markEtlExecutionAsFailedAt(UUID etlExecutionId, OffsetDateTime failedAt, String reason)
            throws EntityNotFoundException {
        EtlExecution etlExecution = etlExecutionRepository
                .findById(etlExecutionId)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecution(etlExecutionId));
        EtlExecutionStatus status = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.FAILED)
                .orElseThrow(() -> EntityNotFoundException.forEtlExecutionStatus(EtlExecutionStatus.FAILED));
        log.debug("The status '{}' was found in the repository", EtlExecutionStatus.FAILED);
        etlExecution.setStatus(status);
        etlExecution.setFinishedAt(failedAt);
        etlExecution.setComment(reason);
        etlExecutionRepository.save(etlExecution);
        log.debug("The entity is saved");
    }

}
