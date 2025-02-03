package etl.engine.ems.mapper;

import etl.engine.ems.dao.entity.ExternalSystem;
import etl.engine.ems.model.ExternalSystemDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface ExternalSystemMapper {

    ExternalSystem toEntity(ExternalSystemDto externalSystemDto);

    ExternalSystemDto toExternalSystemDto(ExternalSystem externalSystem);
}