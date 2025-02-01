package etl.engine.ems.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public class EtlExecution extends AuditableEntity implements Serializable {

    @Id
    private UUID id;

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;
    private OffsetDateTime phaseStartedEt;
    private OffsetDateTime phaseFinishedAt;
    private Long totalMessagesProcessed;
    private Long totalMessagesFailed;
    private UUID serviceInstanceId;

    private EtlProcess etlProcess;
    private EtlPhase etlPhase;
}
