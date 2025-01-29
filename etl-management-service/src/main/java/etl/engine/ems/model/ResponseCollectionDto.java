package etl.engine.ems.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * The class is used when a collection of objects should be returned via REST API.
 * @param <T> a type of the returned objects
 */
@Getter
@ToString
public class ResponseCollectionDto<T> extends ResponseDto {

    private final List<T> payload;

    public ResponseCollectionDto(String status, OffsetDateTime timestamp, String description, List<T> payload) {
        super(status, timestamp, description);
        this.payload = payload;
    }
}
