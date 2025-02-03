package etl.engine.ems.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value("${ems.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${ems.kafka.consumer.group}")
    private String consumerGroupName;

    @Value("${ems.kafka.topics.heartbeat}")
    private String heartBeatTopicName;

    @Value("${ems.kafka.topics.control}")
    private String controlTopicName;

    @Value("${ems.kafka.topics.progress}")
    private String progressTopicName;

    public Map<String, Object> consumerConfiguration() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configuration.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerGroupName);
        configuration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configuration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return configuration;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfiguration());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        return factory;
    }

    /**
     * The parameter spring.kafka.bootstrap-servers is not used in the application.yaml.
     * That's the KafkaAdmin client should be created here.
     * @return KafkaAdmin
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configuration);
    }

    /**
     * EMS consumes here instance status reports from ETL-services.
     * @return NewTopic
     */
    @Bean
    public NewTopic heartBeatTopic() {
        return TopicBuilder
                .name(heartBeatTopicName)
                .partitions(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "180000")
                .build();
    }

    /**
     * EMS publishes to the topic commands to run ETL-processes.
     * ETL-services of 'data-extractor' type are the consumers of the topic.
     * @return NewTopic
     */

    @Bean
    public NewTopic controlTopic() {
        return TopicBuilder
                .name(controlTopicName)
                .partitions(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "180000")
                .build();
    }

    /**
     * EMS consumes here status reports from ETL-services about ETL-process execution progress.
     * @return NewTopic
     */
    @Bean
    public NewTopic progressTopic() {
        return TopicBuilder
                .name(progressTopicName)
                .partitions(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "180000")
                .build();
    }

}
