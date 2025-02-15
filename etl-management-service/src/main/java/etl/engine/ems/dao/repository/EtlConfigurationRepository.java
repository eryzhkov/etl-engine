package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.EtlConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EtlConfigurationRepository extends JpaRepository<EtlConfiguration, UUID> {

}
