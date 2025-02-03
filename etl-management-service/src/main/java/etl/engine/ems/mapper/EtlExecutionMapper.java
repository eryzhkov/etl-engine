package etl.engine.ems.mapper;

import etl.engine.ems.dao.entity.EtlExecution;
import etl.engine.ems.model.EtlExecutionDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {
        EtlProcessMapper.class, EtlExecutionStatusMapper.class})
public interface EtlExecutionMapper {

    EtlExecution toEntity(EtlExecutionDto etlExecutionDto);

    EtlExecutionDto toEtlExecutionDto(EtlExecution etlExecution);
}