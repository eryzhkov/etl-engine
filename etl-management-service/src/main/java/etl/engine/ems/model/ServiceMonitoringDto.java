package etl.engine.ems.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import etl.engine.ems.dao.entity.ServiceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link etl.engine.ems.dao.entity.ServiceMonitoring}
 */
@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@ToString
public class ServiceMonitoringDto {
    UUID id;
    String instanceType;
    String instanceState;
    OffsetDateTime reportedAt;
    ServiceStatus status;
    OffsetDateTime statusUpdatedAt;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}