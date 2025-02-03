package etl.engine.ems.controller;

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
import etl.engine.ems.model.EtlExecutionDto;
import etl.engine.ems.model.EtlServiceType;
import etl.engine.ems.model.ResponseCollectionDto;
import etl.engine.ems.model.ResponseSingleDto;
import etl.engine.ems.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/etl-executions")
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionController {

    private final EtlExecutionRepository etlExecutionRepository;
    private final EtlExecutionMapper etlExecutionMapper;
    private final EtlExecutionStatusRepository etlExecutionStatusRepository;
    private final EtlProcessRepository etlProcessRepository;
    private final ServiceMonitoringRepository serviceMonitoringRepository;

    @GetMapping
    public ResponseEntity<ResponseCollectionDto<EtlExecutionDto>> getAllEtlExecutions() {
        List<EtlExecutionDto> etlExecutions = etlExecutionRepository
                .findAll()
                .stream()
                .map(etlExecutionMapper::toEtlExecutionDto)
                .toList();
        final ResponseCollectionDto<EtlExecutionDto> body = new ResponseCollectionDto<>(
                ResponseStatus.ok,
                "Get all ETL-executions",
                etlExecutions
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseSingleDto<EtlExecutionDto>> createEtlExecution(
            @RequestBody EtlExecutionDto etlExecutionDto) throws EntityNotFoundException {
        log.debug("request: {}", etlExecutionDto);
        // Try to find an ETL-service instance of the 'data-extract' type to run the ETL-process.
        ServiceMonitoring etlServiceInstance = serviceMonitoringRepository
                .findServiceMonitoringByStatusAndInstanceStateAndInstanceType(
                        ServiceStatus.online,
                        "idle",
                        EtlServiceType.DATA_EXTRACTOR.toString())
                .orElseThrow(
                        () -> new EntityNotFoundException("There are no suitable ETL-service instance of the 'data-extractor' type in the 'idle' state to create an ETL execution!")
                );

        // Create a new ETL execution entity with the 'CREATED' status.
        UUID etlProcessId = etlExecutionDto.getEtlProcess().getId();
        EtlProcess targetEtlProcess = etlProcessRepository
                .findById(etlProcessId)
                .orElseThrow(
                        () -> new EntityNotFoundException("The required ETL process with id=" + etlProcessId + " was not found in the repository!")
                );
        EtlExecutionStatus createdStatus = etlExecutionStatusRepository
                .findEtlExecutionStatusByName(EtlExecutionStatus.CREATED)
                .orElseThrow(() -> new EntityNotFoundException("The required ETL execution status 'CREATED' was not found in the repository!"));
        EtlExecution etlExecutionEntity = etlExecutionMapper.toEntity(etlExecutionDto);
        etlExecutionEntity.setStatus(createdStatus);
        etlExecutionEntity.setEtlProcess(targetEtlProcess);
        log.debug("New EtlExecution entity: {}", etlExecutionEntity);
        // Save the ETL-execution
        EtlExecution createdEtlExecution = etlExecutionRepository.save(etlExecutionEntity);
        log.debug("Saved EtlExecution entity: {}", createdEtlExecution);
        // Notify the found ETL-service instance about the new ETL-execution
        //TODO Send the command via topic!
        final ResponseSingleDto<EtlExecutionDto> body = new ResponseSingleDto<>(
                ResponseStatus.ok,
                "Create new ETL-execution",
                etlExecutionMapper.toEtlExecutionDto(createdEtlExecution)
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

}
