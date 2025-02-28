package etl.engine.ems.controller;

import etl.engine.ems.dao.entity.ExternalSystem;
import etl.engine.ems.dao.repository.ExternalSystemRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.mapper.ExternalSystemMapper;
import etl.engine.ems.model.ExternalSystemDto;
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
@RequestMapping("/api/v1/external-systems")
@RequiredArgsConstructor
@Slf4j
public class ExternalSystemController {

    private final ExternalSystemRepository externalSystemRepository;
    private final ExternalSystemMapper externalSystemMapper;

    @GetMapping
    public ResponseEntity<ResponseCollectionDto<ExternalSystemDto>> getAllExternalSystems() {
        log.debug("Request to get all external systems");
        List<ExternalSystemDto> externalSystems = externalSystemRepository
                .findAll()
                .stream()
                .map(externalSystemMapper::toExternalSystemDto)
                .toList();
        final ResponseCollectionDto<ExternalSystemDto> body = new ResponseCollectionDto<>(
                ResponseStatus.ok,
                "Get all external systems",
                externalSystems);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseSingleDto<ExternalSystemDto>> createExternalSystem(
            @RequestBody ExternalSystemDto request) {
        log.debug("Request to create new external system with parameters '{}'", request);
        ExternalSystem externalSystem = new ExternalSystem();
        externalSystem.setName(request.getName());
        externalSystem.setCode(request.getCode());
        externalSystem.setDescription(request.getDescription());
        ExternalSystem saved = externalSystemRepository.save(externalSystem);
        log.debug("Saved entity: {}", saved);
        final ResponseSingleDto<ExternalSystemDto> body = new ResponseSingleDto<>(
                ResponseStatus.ok,
                "Create new external system",
                externalSystemMapper.toExternalSystemDto(saved));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseSingleDto<ExternalSystemDto>> updateExternalSystem(
            @PathVariable("id") UUID id, @RequestBody Map<String, String> requestBody
    ) throws EntityNotFoundException {
        log.debug("Request to update the external system with id = '{}' using parameters '{}'", id, requestBody);
        ExternalSystem foundEntity = externalSystemRepository.findById(id).orElseThrow(
                () ->new EntityNotFoundException("The external system with id=" + id + " was not found!")
        );
        log.debug("Found entity to be updated: {}", foundEntity);
        int updatesCount = 0;
        if (!requestBody.isEmpty()) {
            if (requestBody.containsKey("name")) {
                foundEntity.setName(requestBody.get("name"));
                updatesCount++;
            }
            if (requestBody.containsKey("code")) {
                foundEntity.setCode(requestBody.get("code"));
                updatesCount++;
            }
            if (requestBody.containsKey("description")) {
                foundEntity.setDescription(requestBody.get("description"));
                updatesCount++;
            }
        }
        log.debug("Found {} updates", updatesCount);
        if (updatesCount > 0) {
            ExternalSystem updatedEntity = externalSystemRepository.save(foundEntity);
            log.debug("The found entity was updated.");

            final ResponseSingleDto<ExternalSystemDto> body = new ResponseSingleDto<>(
                    ResponseStatus.ok,
                    "Update the external system",
                    externalSystemMapper.toExternalSystemDto(updatedEntity));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cacheControl(CacheControl.noCache())
                    .body(body);
        } else {
            log.debug("No updates were found.");
            final ResponseSingleDto<ExternalSystemDto> body = new ResponseSingleDto<>(
                    ResponseStatus.warning,
                    "The external system was not updated due to the empty or incorrect request body",
                    externalSystemMapper.toExternalSystemDto(foundEntity));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cacheControl(CacheControl.noCache())
                    .body(body);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseSingleDto<ExternalSystemDto>> deleteExternalSystem(@PathVariable("id") UUID id) throws EntityNotFoundException {
        log.debug("Request to delete the external system with id = '{}'", id);
        ExternalSystem foundEntity = externalSystemRepository.findById(id).orElseThrow(
                () ->new EntityNotFoundException("The external system with id=" + id + " was not found!")
        );
        log.debug("Found entity to be deleted: {}", foundEntity);
        externalSystemRepository.deleteById(id);
        log.debug("The entity was deleted.");
        final ResponseSingleDto<ExternalSystemDto> body = new ResponseSingleDto<>(
                ResponseStatus.ok,
                "Delete the external system",
                externalSystemMapper.toExternalSystemDto(foundEntity));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

}
