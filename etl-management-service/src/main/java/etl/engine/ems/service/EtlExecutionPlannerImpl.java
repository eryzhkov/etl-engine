package etl.engine.ems.service;

import etl.engine.ems.dao.entity.EtlExecution;
import etl.engine.ems.dao.entity.EtlExecutionStatus;
import etl.engine.ems.dao.entity.EtlProcess;
import etl.engine.ems.dao.entity.ServiceMonitoring;
import etl.engine.ems.dao.entity.ServiceStatus;
import etl.engine.ems.dao.repository.EtlExecutionRepository;
import etl.engine.ems.dao.repository.EtlExecutionStatusRepository;
import etl.engine.ems.dao.repository.EtlProcessRepository;
import etl.engine.ems.dao.repository.ServiceMonitoringRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.mapper.EtlExecutionMapper;
import etl.engine.ems.mapper.ServiceMonitoringMapper;
import etl.engine.ems.model.EtlExecutionDto;
import etl.engine.ems.model.EtlServiceType;
import etl.engine.ems.model.ServiceMonitoringDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionPlannerImpl implements EtlExecutionPlanner {

    private final ServiceMonitoringRepository serviceMonitoringRepository;
    private final ServiceMonitoringMapper serviceMonitoringMapper;
    private final EtlExecutionStatusRepository etlExecutionStatusRepository;
    private final EtlProcessRepository etlProcessRepository;
    private final EtlExecutionRepository etlExecutionRepository;
    private final EtlExecutionMapper etlExecutionMapper;

    @Override
    public ServiceMonitoringDto getFreeDataExtractorInstance() throws EntityNotFoundException {
        ServiceMonitoring etlDataExtractorIdleInstance = serviceMonitoringRepository
                .findServiceMonitoringByStatusAndInstanceStateAndInstanceType(
                        ServiceStatus.online,
                        "idle",
                        EtlServiceType.DATA_EXTRACTOR.toString())
                .orElseThrow(
                        () -> new EntityNotFoundException("There are no suitable ETL-service instance of the 'data-extractor' type in the 'idle' state to create an ETL execution!")
                );
        return serviceMonitoringMapper.toServiceMonitoringDto(etlDataExtractorIdleInstance);
    }

    @Transactional
    @Override
    public EtlExecutionDto createEtlProcessExecution(UUID etlProcessId) throws EntityNotFoundException {
        log.debug("Try to start ETL-process with the id='{}'.", etlProcessId);
        ServiceMonitoringDto serviceMonitoringDto = getFreeDataExtractorInstance();
        EtlExecutionStatus createdStatus = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.CREATED)
                .orElseThrow(() -> new EntityNotFoundException("The required ETL execution status 'CREATED' was not found in the repository!"));
        log.debug("The status 'CHECKED' was found in the repository");
        EtlProcess etlProcessToBeRun = etlProcessRepository
                .findById(etlProcessId)
                .orElseThrow(
                        () -> new EntityNotFoundException("The required ETL process with id=" + etlProcessId + " was not found in the repository!")
                );
        log.debug("The ETL-process was found in the repository.");
        EtlExecution etlExecution = new EtlExecution();
        etlExecution.setEtlProcess(etlProcessToBeRun);
        etlExecution.setStatus(createdStatus);
        EtlExecution savedEtlExecution = etlExecutionRepository.save(etlExecution);
        log.debug("ETL-execution was created: {}", savedEtlExecution);
        // Notify the found ETL-service instance about the new ETL-execution
        //TODO Send the command via topic!
        return etlExecutionMapper.toEtlExecutionDto(savedEtlExecution);
    }
}
