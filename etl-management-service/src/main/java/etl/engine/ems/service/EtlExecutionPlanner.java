package etl.engine.ems.service;

import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.model.EtlExecutionDto;

import java.util.UUID;

public interface EtlExecutionPlanner {
    EtlExecutionDto createEtlProcessExecution(UUID etlProcessId) throws EntityNotFoundException;
}
