package etl.engine.ems.mapper;

import etl.engine.ems.dao.entity.EtlExecutionStatus;
import etl.engine.ems.model.EtlExecutionStatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface EtlExecutionStatusMapper {

    EtlExecutionStatus toEntity(EtlExecutionStatusDto etlExecutionStatusDto);

    EtlExecutionStatusDto toEtlExecutionStatusDto(EtlExecutionStatus etlExecutionStatus);
}