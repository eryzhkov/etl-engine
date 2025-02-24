package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.EtlExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtlExecutionRepository extends JpaRepository<EtlExecution, UUID> {

    @Query(value = "select max(e.finished_at) from etl_executions e where e.ref_etl_process_id = ?1 and e.ref_etl_execution_status_id = 4", nativeQuery = true)
    Optional<OffsetDateTime> getLastSuccessfulExecutionTimestamp(@NonNull UUID etlProcessId);

}
