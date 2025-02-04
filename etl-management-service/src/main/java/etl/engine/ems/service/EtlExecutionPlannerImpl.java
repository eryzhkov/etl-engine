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
import etl.engine.ems.service.messaging.model.EtlStartCommand;
import etl.engine.ems.service.monitoring.instance.EtlInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public EtlExecutionDto createEtlProcessExecution(UUID etlProcessId) throws EntityNotFoundException {
        log.debug("Try to start ETL-process with the id='{}'.", etlProcessId);

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
        EtlExecutionDto etlExecutionDto = etlExecutionMapper.toEtlExecutionDto(createdEtlExecution);
        EtlStartCommand etlStartCommand = new EtlStartCommand(etlInstanceDto.getId(), etlExecutionDto);
        log.debug("The 'etl-start' command to be sent: {}", etlStartCommand);
        kafkaTemplate.send(controlTopicName, etlStartCommand);

        log.debug("ETL-execution is created and the command is published.");
        return etlExecutionMapper.toEtlExecutionDto(createdEtlExecution);
    }
}
