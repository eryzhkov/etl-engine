package etl.engine.extract.service.messaging;

import etl.engine.extract.model.event.Event;

public interface MessagingService {

    <T> void sendInstanceStatusReport(Event<T> instanceStatusReport);

}
