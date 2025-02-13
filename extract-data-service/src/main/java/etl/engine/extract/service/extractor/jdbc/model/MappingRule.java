package etl.engine.extract.service.extractor.jdbc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MappingRule {

    public final static String INTEGER_TYPE = "INTEGER";
    public final static String STRING_TYPE = "VARCHAR";

    private final String type;
    private final String targetAttribute;
    private final Validator validator;

    @JsonCreator(mode = Mode.PROPERTIES)
    public MappingRule(
            @JsonProperty("type") String type,
            @JsonProperty("targetAttribute") String targetAttribute,
            @JsonProperty("validator") Validator validator) {
        this.type = type;
        this.targetAttribute = targetAttribute;
        this.validator = validator;
    }
}
