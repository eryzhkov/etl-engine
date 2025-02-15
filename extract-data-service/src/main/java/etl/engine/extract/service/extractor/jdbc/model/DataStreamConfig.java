package etl.engine.extract.service.extractor.jdbc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class DataStreamConfig {

    private final String name;
    private final Query query;
    private final Map<String, MappingRule> mappings;

    @JsonCreator(mode = Mode.PROPERTIES)
    public DataStreamConfig(
            @JsonProperty("name") String name,
            @JsonProperty("query") Query query,
            @JsonProperty("mappings") Map<String, MappingRule> mappings) {
        this.name = name;
        this.query = query;
        this.mappings = mappings;
    }
}
