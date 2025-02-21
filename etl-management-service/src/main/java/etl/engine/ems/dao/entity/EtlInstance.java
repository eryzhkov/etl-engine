package etl.engine.ems.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
@Table(name = "etl_instances")
@Getter
@Setter
@ToString(callSuper = true)
public class EtlInstance extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "instance_id")
    private UUID id;

    @Column(name = "instance_state", nullable = false)
    private String instanceState;

    @Column(name = "reported_at", nullable = false)
    private OffsetDateTime reportedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ServiceStatus status;

    @Column(name = "status_updated_at")
    private OffsetDateTime statusUpdatedAt;

    public EtlInstance() {
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
        EtlInstance that = (EtlInstance) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass()
                .hashCode()
                : getClass().hashCode();
    }
}
