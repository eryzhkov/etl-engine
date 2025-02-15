package etl.engine.ems.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString
public class ResponseInfo {
    private final ResponseStatus status;
    private final OffsetDateTime timestamp;
    private final String description;

    public ResponseInfo(ResponseStatus status, String description) {
        this.status = status;
        this.timestamp = OffsetDateTime.now();
        this.description = description;
    }
}
