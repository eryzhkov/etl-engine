package etl.engine.ems.service.messaging.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EtlNotification<T> {

    public static final String ETL_EXECUTION_ACCEPTED = "etl-execution-accepted";
    public static final String ETL_EXECUTION_STARTED = "etl-execution-started";
    public static final String ETL_EXECUTION_FINISHED = "etl-execution-finished";
    public static final String ETL_EXECUTION_FAILED = "etl-execution-failed";
    public static final String ETL_DATA_STREAM_STARTED = "etl-data-stream-started";
    public static final String ETL_DATA_STREAM_FINISHED = "etl-data-stream-finished";
    public static final String ETL_DATA_STREAM_FAILED = "etl-data-stream-failed";
    public static final String ETL_DATA_STREAM_STATS = "etl-data-stream-stats";

    private final EtlNotificationInfo info;
    private final T payload;

    public EtlNotification(String notification, T payload) {
        this.info = new EtlNotificationInfo(notification);
        this.payload = payload;
    }
}
