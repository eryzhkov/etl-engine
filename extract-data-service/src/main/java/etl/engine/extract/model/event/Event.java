package etl.engine.extract.model.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class Event<T> {

    private final EventInfo info;
    private final T payload;

}
