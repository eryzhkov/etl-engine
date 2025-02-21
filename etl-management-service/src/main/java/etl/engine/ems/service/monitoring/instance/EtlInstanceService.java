package etl.engine.ems.service.monitoring.instance;

import etl.engine.ems.dao.entity.EtlInstance;
import etl.engine.ems.dao.entity.ServiceStatus;
import etl.engine.ems.dao.repository.EtlInstanceRepository;
import etl.engine.ems.exception.EntityNotFoundException;
import etl.engine.ems.mapper.EtlInstanceMapper;
import etl.engine.ems.model.EtlInstanceDto;
import etl.engine.ems.model.InstanceStatusReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class EtlInstanceService {

    private final EtlInstanceRepository etlInstanceRepository;
    private final EtlInstanceMapper etlInstanceMapper;

    @Transactional(propagation = Propagation.NESTED)
    public void saveInstanceStatusReport(InstanceStatusReport report) {
        EtlInstance entity = new EtlInstance();
        entity.setId(report.getInstanceId());
        entity.setInstanceState(report.getInstanceState());
        entity.setReportedAt(report.getReportedAt());
        entity.setStatus(ServiceStatus.online);
        entity.setStatusUpdatedAt(OffsetDateTime.now());
        etlInstanceRepository.save(entity);
    }

    public EtlInstanceDto getFreeDataExtractorInstance() throws EntityNotFoundException {
        EtlInstance etlDataExtractorIdleInstance = etlInstanceRepository
                .findEtlInstanceByStatusAndInstanceState(
                        ServiceStatus.online,
                        "idle")
                .orElseThrow(
                        () -> new EntityNotFoundException("There are no suitable ETL-service instance of the 'data-extractor' type in the 'idle' state to create an ETL execution!")
                );
        return etlInstanceMapper.toEtlInstanceDto(etlDataExtractorIdleInstance);
    }

}
