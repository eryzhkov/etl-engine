package etl.engine.extract.model.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class EventInfo {

    private final String type;
    private final String signal;

}
