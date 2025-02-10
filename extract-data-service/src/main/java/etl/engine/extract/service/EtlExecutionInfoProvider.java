package etl.engine.extract.service;

import etl.engine.extract.exception.EtlConfigurationLoadException;
import etl.engine.extract.exception.EtlConfigurationParseException;
import etl.engine.extract.model.EtlExecutionInfo;

import java.util.UUID;

public interface EtlExecutionInfoProvider {

    public EtlExecutionInfo getExecutionInfo(String externalSystemCode, String etlProcessCode, UUID etlExecutionId)
            throws EtlConfigurationParseException, EtlConfigurationLoadException;

}
