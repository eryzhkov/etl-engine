package etl.engine.worker.service.execution.processor;

import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

@Slf4j
public class RowToJsonProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
      log.info("exchange: {}", exchange);
        Map<String, Object> properties = exchange.getAllProperties();
        Message message = exchange.getMessage();
        for(Entry<String, Object> entry : properties.entrySet()) {
            log.info("key = {}, value = {}", entry.getKey(), entry.getValue());
        }
        log.info("Message body: {}", message.getBody());
    }
}
