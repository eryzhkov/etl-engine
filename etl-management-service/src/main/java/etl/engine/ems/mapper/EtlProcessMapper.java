package etl.engine.ems.mapper;

import etl.engine.ems.dao.entity.EtlProcess;
import etl.engine.ems.model.EtlProcessDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {
        ExternalSystemMapper.class})
public interface EtlProcessMapper {

    EtlProcess toEntity(EtlProcessDto etlProcessDto);

    EtlProcessDto toEtlProcessDto(EtlProcess etlProcess);
}