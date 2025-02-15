package etl.engine.ems.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import etl.engine.ems.dao.entity.EtlPhase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link etl.engine.ems.dao.entity.EtlStreamExecution}
 */
@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@ToString
public class EtlStreamExecutionDto {

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    UUID id;
    UUID serviceInstanceId;
    String streamName;
    OffsetDateTime streamStartedAt;
    OffsetDateTime streamFinishedAt;
    OffsetDateTime streamFailedAt;
    String comment;
    Long totalInMessages;
    Long totalOutMessages;
    Long totalFailedMessages;
    EtlExecutionDto etlExecution;
    EtlPhase etlPhase;
}