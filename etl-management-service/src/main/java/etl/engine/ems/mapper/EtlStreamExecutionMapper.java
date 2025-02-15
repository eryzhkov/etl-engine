package etl.engine.ems.mapper;

import etl.engine.ems.dao.entity.EtlStreamExecution;
import etl.engine.ems.model.EtlStreamExecutionDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {
        EtlExecutionMapper.class})
public interface EtlStreamExecutionMapper {

    EtlStreamExecution toEntity(EtlStreamExecutionDto etlStreamExecutionDto);

    EtlStreamExecutionDto toEtlStreamExecutionDto(EtlStreamExecution etlStreamExecution);
}