package etl.engine.ems.model;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * The class is used when a single object should be returned via REST API.
 * @param <T> a type of the returned object
 */
@Getter
@ToString
public class ResponseSingleDto<T> extends ResponseDto {

    private final T payload;

    public ResponseSingleDto(String status, LocalDateTime timestamp, String description, T payload) {
        super(status, timestamp, description);
        this.payload = payload;
    }

}
