package etl.engine.worker.config;

import etl.engine.worker.service.instance.InstanceInfoManager;
import etl.engine.worker.service.messaging.CustomJsonSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
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
@RequiredArgsConstructor
public class KafkaConfiguration {

    @Value("${ews.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${ews.kafka.consumer.group}")
    private String consumerGroupName;

    private final InstanceInfoManager instanceInfoManager;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomJsonSerializer.class);

        DefaultKafkaProducerFactory<String, Object> defaultKafkaProducerFactory = new DefaultKafkaProducerFactory<>(configuration);
        defaultKafkaProducerFactory.setTransactionIdPrefix("ews-tx-" + instanceInfoManager.getInstanceId());
        return defaultKafkaProducerFactory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configuration.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName + "-" + instanceInfoManager.getInstanceId());
        configuration.put(ConsumerConfig.CLIENT_ID_CONFIG, "ews-" + instanceInfoManager.getInstanceId());
        configuration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configuration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configuration.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configuration.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return new DefaultKafkaConsumerFactory<>(configuration);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Only one topic will be consumed.
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

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager() {
        return new KafkaTransactionManager<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
