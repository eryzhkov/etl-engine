package etl.engine.ems.controller;

import etl.engine.ems.dao.entity.EtlPhase;
import etl.engine.ems.dao.repository.EtlPhaseRepository;
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
@RequestMapping("/api/v1/etl-phases")
@RequiredArgsConstructor
@Slf4j
public class EtlPhaseController {

    private final EtlPhaseRepository etlPhaseRepository;

    @GetMapping
    public ResponseEntity<ResponseCollectionDto<EtlPhase>> getAllEtlPhases() {
        List<EtlPhase> etlPhases = etlPhaseRepository.findAll();

        final ResponseCollectionDto<EtlPhase> body = new ResponseCollectionDto<>(
                ResponseStatus.ok,
                "Get all ETL-phases",
                etlPhases);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

}
