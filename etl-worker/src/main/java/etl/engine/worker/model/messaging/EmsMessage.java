package etl.engine.worker.model.messaging;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EmsMessage<I, P> {

    private final I info;
    private final P payload;

    public EmsMessage(I info, P payload) {
        this.info = info;
        this.payload = payload;
    }

}
