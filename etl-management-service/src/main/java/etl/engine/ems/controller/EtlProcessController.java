package etl.engine.ems.controller;

import etl.engine.ems.dao.entity.EtlProcess;
import etl.engine.ems.dao.entity.ExternalSystem;
import etl.engine.ems.dao.repository.EtlProcessRepository;
import etl.engine.ems.dao.repository.ExternalSystemRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.mapper.EtlProcessMapper;
import etl.engine.ems.model.EtlProcessDto;
import etl.engine.ems.model.ResponseCollectionDto;
import etl.engine.ems.model.ResponseSingleDto;
import etl.engine.ems.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/etl-processes")
@RequiredArgsConstructor
@Slf4j
public class EtlProcessController {

    private final EtlProcessRepository etlProcessRepository;
    private final EtlProcessMapper etlProcessMapper;
    private final ExternalSystemRepository externalSystemRepository;

    @GetMapping
    public ResponseEntity<ResponseCollectionDto<EtlProcessDto>> getAllEtlProcesses() {
      log.debug("Request for all ETL-processes");
        List<EtlProcessDto> etlProcesses = etlProcessRepository
                .findAll()
                .stream()
                .map(etlProcessMapper::toEtlProcessDto)
                .toList();
        final ResponseCollectionDto<EtlProcessDto> body = new ResponseCollectionDto<>(
                ResponseStatus.ok,
                "Get all ETL-processes",
                etlProcesses);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseSingleDto<EtlProcessDto>> createEtlProcess(@RequestBody EtlProcessDto request)
            throws EntityNotFoundException {
        log.debug("Request to create new ETL-process with parameters '{}'", request);
        ExternalSystem externalSystem = externalSystemRepository.findById(request.getExternalSystem().getId()).orElseThrow(
                () -> new EntityNotFoundException("The external system with id=" + request.getExternalSystem().getId() + " was not found!")
        );
        EtlProcess etlProcess = new EtlProcess();
        etlProcess.setName(request.getName());
        etlProcess.setCode(request.getCode());
        etlProcess.setDescription(request.getDescription());
        etlProcess.setExternalSystem(externalSystem);
        EtlProcess savedEtlProcess = etlProcessRepository.save(etlProcess);
        log.debug("Saved entity {}", savedEtlProcess);
        final ResponseSingleDto<EtlProcessDto> body = new ResponseSingleDto<>(
                ResponseStatus.ok,
                "Create new ETL-process",
                etlProcessMapper.toEtlProcessDto(savedEtlProcess));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseSingleDto<EtlProcessDto>> createEtlProcess(
            @PathVariable("id") UUID id,
            @RequestBody Map<String, String> requestBody)
            throws EntityNotFoundException {
        //TODO Add the possibility to change external system later.
        log.debug("Request to update the ETL-process with id = '{}' using parameters '{}'", id, requestBody);
        EtlProcess etlProcess = etlProcessRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("The ETL-process with id=" + id + " was not found!")
        );
        log.debug("Found entity to be updated: {}", etlProcess);
        int updatesCount = 0;
        if (!requestBody.isEmpty()) {
            if (requestBody.containsKey("name")) {
                etlProcess.setName(requestBody.get("name"));
                updatesCount++;
            }
            if (requestBody.containsKey("code")) {
                etlProcess.setCode(requestBody.get("code"));
                updatesCount++;
            }
            if (requestBody.containsKey("description")) {
                etlProcess.setDescription(requestBody.get("description"));
                updatesCount++;
            }
        }
        log.debug("Found {} updates", updatesCount);
        if (updatesCount > 0) {
            EtlProcess updatedEtlProcess = etlProcessRepository.save(etlProcess);
            log.debug("The found entity was updated.");
            final ResponseSingleDto<EtlProcessDto> body = new ResponseSingleDto<>(
                    ResponseStatus.ok,
                    "Update the ETL-process",
                    etlProcessMapper.toEtlProcessDto(updatedEtlProcess));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cacheControl(CacheControl.noCache())
                    .body(body);
        } else {
            log.debug("No updates were found.");
            final ResponseSingleDto<EtlProcessDto> body = new ResponseSingleDto<>(
                    ResponseStatus.warning,
                    "The ETL-processm was not updated due to the empty or incorrect request body",
                    etlProcessMapper.toEtlProcessDto(etlProcess));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cacheControl(CacheControl.noCache())
                    .body(body);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseSingleDto<EtlProcessDto>> deleteEtlProcess(@PathVariable("id") UUID id)  throws EntityNotFoundException {
        log.debug("Request to delete the ETL-process with id = '{}'", id);
        EtlProcess etlProcess = etlProcessRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("The ETL-process with id=" + id + " was not found!")
        );
        log.debug("Found entity to be deleted: {}", etlProcess);
        etlProcessRepository.deleteById(id);
        log.debug("The entity was deleted.");
        final ResponseSingleDto<EtlProcessDto> body = new ResponseSingleDto<>(
                ResponseStatus.ok,
                "Delete the ETL-process",
                etlProcessMapper.toEtlProcessDto(etlProcess));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

}
