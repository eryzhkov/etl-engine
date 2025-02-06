package etl.engine.ems.service.messaging.model;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class EtlCommand<T> {

    private final CommandInfo info;
    private final T payload;

    public EtlCommand(String command, UUID recipientInstanceId, T payload) {
        this.info = new CommandInfo(command, recipientInstanceId);
        this.payload = payload;
    }

}
