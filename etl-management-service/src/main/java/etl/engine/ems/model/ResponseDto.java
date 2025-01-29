package etl.engine.ems.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@ToString
public abstract class ResponseDto {

    private final ResponseInfo info;

    public ResponseDto(String status, OffsetDateTime timestamp, String description) {
        this.info = new ResponseInfo(status, timestamp, description);
    }

}
