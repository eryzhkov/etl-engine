package etl.engine.ems.service.messaging.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString
public class CommandInfo {

    public static final String ETL_EXECUTION_ASSIGN = "etl-execution-assign";

    private final String command;
    private final UUID assignee;
    private final OffsetDateTime timestamp;

    public CommandInfo(String command, UUID assignee) {
        this.command = command;
        this.assignee = assignee;
        this.timestamp = OffsetDateTime.now();
    }
}
