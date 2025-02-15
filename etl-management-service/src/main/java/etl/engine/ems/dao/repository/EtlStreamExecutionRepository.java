package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.EtlStreamExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtlStreamExecutionRepository extends JpaRepository<EtlStreamExecution, UUID> {

    Optional<EtlStreamExecution> findEtlStreamExecutionByEtlExecution_IdAndEtlPhase_CodeAndStreamName(
            UUID etlExecutionId,
            String phaseCode,
            String dataStreamName);

}
