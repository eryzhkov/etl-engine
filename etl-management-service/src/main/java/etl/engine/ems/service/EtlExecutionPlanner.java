package etl.engine.ems.service;

import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.model.EtlExecutionDto;
import etl.engine.ems.model.ServiceMonitoringDto;

import java.util.UUID;

public interface EtlExecutionPlanner {
    ServiceMonitoringDto getFreeDataExtractorInstance() throws EntityNotFoundException;
    EtlExecutionDto createEtlProcessExecution(UUID etlProcessId) throws EntityNotFoundException;
}
