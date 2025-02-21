package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.EtlInstance;
import etl.engine.ems.dao.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtlInstanceRepository extends JpaRepository<EtlInstance, UUID> {

    Optional<EtlInstance> findEtlInstanceByStatusAndInstanceState(ServiceStatus status, String state);

}
