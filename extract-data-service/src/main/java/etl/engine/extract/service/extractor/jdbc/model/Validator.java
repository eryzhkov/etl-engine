package etl.engine.extract.service.extractor.jdbc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Validator {

    private final boolean required;
    private final int maxLength;

    @JsonCreator(mode = Mode.PROPERTIES)
    public Validator(
            @JsonProperty(value = "required", required = true) boolean required,
            @JsonProperty(value = "maxLength") int maxLength
    ) {
        this.required = required;
        this.maxLength = maxLength;
    }
}
