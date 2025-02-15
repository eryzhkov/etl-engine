package etl.engine.extract.model.messaging;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString
public class EmsMessageInfo {

    public static final String INSTANCE_STATUS = "instance-status";
    public static final String ETL_EXECUTION_ACCEPTED = "etl-execution-accepted";
    public static final String ETL_EXECUTION_STARTED = "etl-execution-started";
    public static final String ETL_EXECUTION_FINISHED = "etl-execution-finished";
    public static final String ETL_EXECUTION_FAILED = "etl-execution-failed";
    public static final String ETL_DATA_STREAM_STARTED = "etl-data-stream-started";
    public static final String ETL_DATA_STREAM_FINISHED = "etl-data-stream-finished";
    public static final String ETL_DATA_STREAM_FAILED = "etl-data-stream-failed";
    public static final String ETL_DATA_STREAM_STATS = "etl-data-stream-stats";

    private final String type;
    private final OffsetDateTime timestamp;

    public EmsMessageInfo(String type) {
        this.type = type;
        this.timestamp = OffsetDateTime.now();
    }

}
