package etl.engine.ems.mapper;

import etl.engine.ems.dao.entity.EtlInstance;
import etl.engine.ems.model.EtlInstanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface EtlInstanceMapper {

    EtlInstance toEntity(EtlInstanceDto etlInstanceDto);

    EtlInstanceDto toEtlInstanceDto(EtlInstance etlInstance);
}