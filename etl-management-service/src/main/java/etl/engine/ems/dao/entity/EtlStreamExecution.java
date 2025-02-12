package etl.engine.ems.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "etl_streams_executions")
@Getter
@Setter
@ToString(callSuper = true)
public class EtlStreamExecution extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "service_instance_id", nullable = false)
    private UUID serviceInstanceId;

    @Column(name = "stream_name", nullable = false)
    private String streamName;

    @Column(name = "stream_started_at", nullable = false)
    private OffsetDateTime streamStartedAt;

    @Column(name = "stream_finished_at")
    private OffsetDateTime streamFinishedAt;

    @Column(name = "stream_failed_at")
    private OffsetDateTime streamFailedAt;

    @Column(name = "comment")
    private String comment;

    @Column(name = "total_in_messages")
    private Long totalInMessages = -1L;

    @Column(name = "total_out_messages")
    private Long totalOutMessages = -1L;

    @Column(name = "total_failed_messages")
    private Long totalFailedMessages = -1L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ref_etl_execution_id")
    private EtlExecution etlExecution;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ref_etl_phase_id")
    private EtlPhase etlPhase;

    public EtlStreamExecution() {
        super();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass =
                o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer()
                        .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass =
                this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer()
                        .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        EtlStreamExecution that = (EtlStreamExecution) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass()
                .hashCode()
                : getClass().hashCode();
    }
}
