package etl.engine.extract.service.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandInfo {

    public static final String ETL_EXECUTION_START = "etl-execution-start";

    private final String command;
    private final UUID recipientInstanceId;
    private final OffsetDateTime timestamp;

}
