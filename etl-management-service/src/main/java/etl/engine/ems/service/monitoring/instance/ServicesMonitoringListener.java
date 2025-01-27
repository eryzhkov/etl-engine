package etl.engine.ems.service.monitoring.instance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * The component listens the topic and updates the table in the database to
 * reflect the actual services state.
 */
@Component
@KafkaListener(groupId = "${ems.kafka.consumer.group}", topics = "${ems.topics.heartbeat}")
@Slf4j
public class ServicesMonitoringListener {

    @KafkaHandler
    void listener(String message) {
        log.info("{}", message);
    }

}
