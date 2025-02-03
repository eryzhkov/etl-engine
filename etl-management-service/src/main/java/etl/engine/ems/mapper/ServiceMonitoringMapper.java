package etl.engine.ems.mapper;

import etl.engine.ems.dao.entity.ServiceMonitoring;
import etl.engine.ems.model.ServiceMonitoringDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface ServiceMonitoringMapper {

    ServiceMonitoring toEntity(ServiceMonitoringDto serviceMonitoringDto);

    ServiceMonitoringDto toServiceMonitoringDto(ServiceMonitoring serviceMonitoring);
}