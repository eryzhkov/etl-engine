package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.EtlExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtlExecutionStatusRepository extends JpaRepository<EtlExecutionStatus, Integer> {

    Optional<EtlExecutionStatus> findEtlExecutionStatusByName(String name);

}
