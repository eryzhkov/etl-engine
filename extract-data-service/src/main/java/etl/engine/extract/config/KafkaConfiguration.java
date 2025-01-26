package etl.engine.extract.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    @Value("${eds.topics.heartbeat}")
    private String heartBeatTopicName;

    @Bean
    public NewTopic heartBeatTopic() {
        return TopicBuilder
                .name(heartBeatTopicName)
                .partitions(1)
                .build();
    }
}
