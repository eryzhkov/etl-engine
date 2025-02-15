package etl.engine.ems.service.messaging.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString
public class CommandInfo {

    public static final String ETL_EXECUTION_START = "etl-execution-start";

    private final String command;
    private final UUID recipientInstanceId;
    private final OffsetDateTime timestamp;

    public CommandInfo(String command, UUID recipientInstanceId) {
        this.command = command;
        this.recipientInstanceId = recipientInstanceId;
        this.timestamp = OffsetDateTime.now();
    }
}
