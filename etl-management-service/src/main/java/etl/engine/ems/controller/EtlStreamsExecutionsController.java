package etl.engine.ems.controller;

import etl.engine.ems.dao.repository.EtlStreamExecutionRepository;
import etl.engine.ems.mapper.EtlStreamExecutionMapper;
import etl.engine.ems.model.EtlStreamExecutionDto;
import etl.engine.ems.model.ResponseCollectionDto;
import etl.engine.ems.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/etl-streams-executions")
@RequiredArgsConstructor
@Slf4j
public class EtlStreamsExecutionsController {

    private final EtlStreamExecutionRepository etlStreamExecutionRepository;
    private final EtlStreamExecutionMapper etlStreamExecutionMapper;

    @GetMapping
    public ResponseEntity<ResponseCollectionDto<EtlStreamExecutionDto>> getAllEtlStreamsExecutions() {
        List<EtlStreamExecutionDto> etlStreamsExecutions = etlStreamExecutionRepository
                .findAll()
                .stream()
                .map(etlStreamExecutionMapper::toEtlStreamExecutionDto)
                .toList();
        final ResponseCollectionDto<EtlStreamExecutionDto> body = new ResponseCollectionDto<>(
                ResponseStatus.ok,
                "Get all ETL-streams executions",
                etlStreamsExecutions
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

}
