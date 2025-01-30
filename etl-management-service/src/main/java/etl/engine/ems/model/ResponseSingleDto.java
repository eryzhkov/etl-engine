package etl.engine.ems.model;

import lombok.Getter;
import lombok.ToString;

/**
 * The class is used when a single object should be returned via REST API.
 * @param <T> a type of the returned object
 */
@Getter
@ToString
public class ResponseSingleDto<T> extends ResponseDto {

    private final T payload;

    public ResponseSingleDto(ResponseStatus status, String description, T payload) {
        super(status, description);
        this.payload = payload;
    }

}
