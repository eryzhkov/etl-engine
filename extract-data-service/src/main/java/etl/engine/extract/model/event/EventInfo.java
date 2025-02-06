package etl.engine.extract.model.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class EventInfo {

    public static final String NOTIFICATION_INSTANCE_STATUS = "instance-status";

    private final String notification;
    private final OffsetDateTime timestamp;

    public EventInfo(String notification) {
        this.notification = notification;
        this.timestamp = OffsetDateTime.now();
    }

}
