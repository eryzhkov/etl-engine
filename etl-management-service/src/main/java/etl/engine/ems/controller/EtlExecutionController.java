package etl.engine.ems.controller;

import etl.engine.ems.dao.repository.EtlExecutionRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.mapper.EtlExecutionMapper;
import etl.engine.ems.model.EtlExecutionDto;
import etl.engine.ems.model.ResponseCollectionDto;
import etl.engine.ems.model.ResponseSingleDto;
import etl.engine.ems.model.ResponseStatus;
import etl.engine.ems.service.EtlExecutionPlanner;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/etl-executions")
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionController {

    private final EtlExecutionRepository etlExecutionRepository;
    private final EtlExecutionMapper etlExecutionMapper;
    private final EtlExecutionPlanner etlExecutionPlanner;

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
            @RequestBody Map<String, UUID> requestBody) throws EntityNotFoundException, IOException {
        log.debug("requestBody: {}", requestBody);

        if (requestBody.containsKey("etlProcessId")) {
            UUID etlProcessId = requestBody.get("etlProcessId");
            log.debug("Found etlProcessId = '{}'", etlProcessId);
            EtlExecutionDto etlExecutionDto = etlExecutionPlanner.createEtlProcessExecution(etlProcessId);
            final ResponseSingleDto<EtlExecutionDto> body = new ResponseSingleDto<>(
                    ResponseStatus.ok,
                    "Create new ETL-execution",
                    etlExecutionDto
            );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cacheControl(CacheControl.noCache())
                    .body(body);
        } else {
            log.debug("No etlProcessId was found in the request body");
            final ResponseSingleDto<EtlExecutionDto> body = new ResponseSingleDto<>(
                    ResponseStatus.warning,
                    "The ETL-execution was not created due to the missed ETL-process identifier in the request body",
                    null);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cacheControl(CacheControl.noCache())
                    .body(body);
        }


    }

}
