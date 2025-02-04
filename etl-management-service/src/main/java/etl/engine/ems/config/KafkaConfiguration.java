package etl.engine.ems.config;

import etl.engine.ems.service.messaging.CustomJsonSerializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

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
        configuration.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName);
        configuration.put(ConsumerConfig.CLIENT_ID_CONFIG, "ems");
        configuration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configuration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configuration.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configuration.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
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

    @Bean
    public Map<String, Object> producerConfiguration() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomJsonSerializer.class);
        return configuration;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        DefaultKafkaProducerFactory<String, Object> defaultKafkaProducerFactory = new DefaultKafkaProducerFactory<>(producerConfiguration());
        defaultKafkaProducerFactory.setTransactionIdPrefix("ems-tx-");
        return defaultKafkaProducerFactory;
    }

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager() {
        return new KafkaTransactionManager<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
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
