package etl.engine.ems.service.messaging.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString
public class CommandInfo {
    private final String action;
    private final UUID recipientInstanceId;
    private final OffsetDateTime timestamp;

    public CommandInfo(String action, UUID recipientInstanceId) {
        this.action = action;
        this.recipientInstanceId = recipientInstanceId;
        this.timestamp = OffsetDateTime.now();
    }
}
