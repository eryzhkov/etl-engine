package etl.engine.extract.model.messaging;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
public class EmsMessageCommandInfo extends EmsMessageInfo {

    public static final String ETL_EXECUTION_START_TYPE = "etl-execution-start";

    private final UUID recipientInstanceId;

    public EmsMessageCommandInfo(String type, UUID recipientInstanceId) {
        super(type);
        this.recipientInstanceId = recipientInstanceId;
    }

}
