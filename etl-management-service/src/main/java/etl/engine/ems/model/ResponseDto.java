package etl.engine.ems.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class ResponseDto {

    private final ResponseInfo info;

    public ResponseDto(ResponseStatus status, String description) {
        this.info = new ResponseInfo(status, description);
    }

}
