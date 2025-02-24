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

    @Query("select max(e.startedAt) from EtlExecution e where e.etlProcess.id = ?1 and e.status.id = 4")
    Optional<OffsetDateTime> getLastSuccessfulRunAtTimestamp(@NonNull UUID etlProcessId);

}
