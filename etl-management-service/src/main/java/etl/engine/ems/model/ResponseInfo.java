package etl.engine.ems.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString
public class ResponseInfo {
    private final String status;
    private final OffsetDateTime timestamp;
    private final String description;

    public ResponseInfo(String status, String description) {
        this.status = status;
        this.timestamp = OffsetDateTime.now();
        this.description = description;
    }
}
