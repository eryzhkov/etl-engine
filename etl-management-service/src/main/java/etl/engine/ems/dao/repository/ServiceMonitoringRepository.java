package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.ServiceMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceMonitoringRepository extends JpaRepository<ServiceMonitoring, UUID> {

}
