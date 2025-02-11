package etl.engine.extract.model.messaging;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString
public class EmsMessageInfo {

    public static final String INSTANCE_STATUS_TYPE = "instance-status";
    public static final String ETL_EXECUTION_ACCEPTED_TYPE = "etl-execution-accepted";
    public static final String ETL_EXECUTION_STARTED_TYPE = "etl-execution-started";
    public static final String ETL_EXECUTION_FINISHED_TYPE = "etl-execution-finished";
    public static final String ETL_EXECUTION_FAILED_TYPE = "etl-execution-failed";

    private final String type;
    private final OffsetDateTime timestamp;

    public EmsMessageInfo(String type) {
        this.type = type;
        this.timestamp = OffsetDateTime.now();
    }

}
