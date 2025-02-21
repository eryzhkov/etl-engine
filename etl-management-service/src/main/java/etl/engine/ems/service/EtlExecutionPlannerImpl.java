package etl.engine.ems.service;

import etl.engine.ems.dao.entity.EtlExecution;
import etl.engine.ems.dao.entity.EtlExecutionStatus;
import etl.engine.ems.dao.entity.EtlProcess;
import etl.engine.ems.dao.repository.EtlExecutionRepository;
import etl.engine.ems.dao.repository.EtlExecutionStatusRepository;
import etl.engine.ems.dao.repository.EtlProcessRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.mapper.EtlExecutionMapper;
import etl.engine.ems.model.EtlExecutionDto;
import etl.engine.ems.model.EtlInstanceDto;
import etl.engine.ems.service.messaging.model.CommandInfo;
import etl.engine.ems.service.messaging.model.EtlCommand;
import etl.engine.ems.service.messaging.model.EtlExecutionAssignCommandPayload;
import etl.engine.ems.service.monitoring.instance.EtlInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionPlannerImpl implements EtlExecutionPlanner {

    @Value("${ems.kafka.topics.control}")
    private String controlTopicName;

    private final EtlInstanceService etlInstanceService;
    private final EtlExecutionStatusRepository etlExecutionStatusRepository;
    private final EtlProcessRepository etlProcessRepository;
    private final EtlExecutionRepository etlExecutionRepository;
    private final EtlExecutionMapper etlExecutionMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    @Override
    public EtlExecutionDto createEtlProcessExecution(UUID etlProcessId) throws EntityNotFoundException, IOException {
        log.debug("Try to start ETL-process with the id='{}'.", etlProcessId);

        //TODO Check if the ETL-process is already running (how many executions are permitted?).
        //TODO Check the possibility to run ETL-process (may be it's not active/blocked/deleted).
        EtlInstanceDto etlInstanceDto = etlInstanceService.getFreeDataExtractorInstance();

        EtlExecutionStatus scheduledStatus = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.SCHEDULED)
                .orElseThrow(() -> new EntityNotFoundException("The required ETL execution status 'SCHEDULED' was not found in the repository!"));
        log.debug("The status 'SCHEDULED' was found in the repository");

        EtlProcess etlProcessToBeRun = etlProcessRepository
                .findById(etlProcessId)
                .orElseThrow(
                        () -> new EntityNotFoundException("The required ETL process with id=" + etlProcessId + " was not found in the repository!")
                );
        log.debug("The ETL-process was found in the repository.");

        EtlExecution etlExecution = new EtlExecution();
        etlExecution.setEtlProcess(etlProcessToBeRun);
        etlExecution.setStatus(scheduledStatus);
        etlExecution.setScheduledAt(OffsetDateTime.now());
        EtlExecution createdEtlExecution = etlExecutionRepository.save(etlExecution);
        log.debug("ETL-execution was created: {}", createdEtlExecution);

        // Notify the found ETL-service instance about the new ETL-execution
        //TODO The EDS should be also notified about the last successful run date-time!
        EtlExecutionAssignCommandPayload payload = EtlExecutionAssignCommandPayload
                .builder()
                .etlExecutionId(createdEtlExecution.getId())
                .configuration(StubEtlConfigurationProvider.getStubEtlConfiguration())
                .build();
        EtlCommand<EtlExecutionAssignCommandPayload> etlStartCommand = new EtlCommand<>(
                CommandInfo.ETL_EXECUTION_ASSIGN,
                etlInstanceDto.getId(),
                payload
        );
        log.debug("The 'etl-execution-assign' command to be published: {}", etlStartCommand);
        kafkaTemplate.send(controlTopicName, etlStartCommand);
        log.debug("The 'etl-execution-assign' command was published.");
        return etlExecutionMapper.toEtlExecutionDto(createdEtlExecution);
    }
}
