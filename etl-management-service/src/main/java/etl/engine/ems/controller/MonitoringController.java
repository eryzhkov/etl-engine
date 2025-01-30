package etl.engine.ems.controller;

import etl.engine.ems.dao.entity.ServiceMonitoring;
import etl.engine.ems.dao.repository.ServiceMonitoringRepository;
import etl.engine.ems.model.ResponseCollectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MonitoringController {

    private final ServiceMonitoringRepository serviceMonitoringRepository;

    @GetMapping(value = "/api/v1/monitoring/services")
    public ResponseEntity<ResponseCollectionDto<ServiceMonitoring>> serviceMonitoring() {

        List<ServiceMonitoring> services = serviceMonitoringRepository.findAll();

        final ResponseCollectionDto<ServiceMonitoring> body = new ResponseCollectionDto<>(
                "ok",
                "Get all service instances",
                services);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

}
