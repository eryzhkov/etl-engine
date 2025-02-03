package etl.engine.ems.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link etl.engine.ems.dao.entity.EtlProcess}
 */
@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@ToString
public class EtlProcessDto {
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    UUID id;
    String name;
    String code;
    String description;
    ExternalSystemDto externalSystem;
}