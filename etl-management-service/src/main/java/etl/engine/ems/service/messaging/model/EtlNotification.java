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

    private final EtlNotificationInfo info;
    private final T payload;

    public EtlNotification(String notification, T payload) {
        this.info = new EtlNotificationInfo(notification);
        this.payload = payload;
    }
}
