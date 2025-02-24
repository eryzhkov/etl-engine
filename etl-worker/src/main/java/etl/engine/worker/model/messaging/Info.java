package etl.engine.worker.model.messaging;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString
public class Info {

    public static final String HEARTBEAT = "heartbeat";
    public static final String ETL_EXECUTION_ASSIGN = "etl-execution-assign";
    public static final String ETL_EXECUTION_ACCEPTED = "etl-execution-accepted";
    public static final String ETL_EXECUTION_STARTED = "etl-execution-started";
    public static final String ETL_EXECUTION_FINISHED = "etl-execution-finished";
    public static final String ETL_EXECUTION_FAILED = "etl-execution-failed";
    public static final String ETL_EXECUTION_REJECTED = "etl-execution-rejected";

    private final String type;
    private final OffsetDateTime timestamp;

    public Info(String type) {
        this.type = type;
        this.timestamp = OffsetDateTime.now();
    }

}
