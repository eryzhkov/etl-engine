package etl.engine.ems.controller;

import etl.engine.ems.dao.entity.EtlInstance;
import etl.engine.ems.dao.repository.EtlInstanceRepository;
import etl.engine.ems.model.ResponseCollectionDto;
import etl.engine.ems.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/etl-instances")
@RequiredArgsConstructor
public class EtlInstanceController {

    private final EtlInstanceRepository etlInstanceRepository;

    @GetMapping
    public ResponseEntity<ResponseCollectionDto<EtlInstance>> getAllEtlInstances() {

        List<EtlInstance> etlInstances = etlInstanceRepository.findAll();

        final ResponseCollectionDto<EtlInstance> body = new ResponseCollectionDto<>(
                ResponseStatus.ok,
                "Get all ETL-instances",
                etlInstances);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

}
