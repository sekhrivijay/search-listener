package com.micro.services.search.listener.index.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

//import org.apache.log4j.Logger;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

//    private KafkaProducer kafkaProducer;

    @Value("${service.kafkaBootstrapServers}")
    private String kafkaBootStrapServers;

    @Value("${service.kafkaProductGroup}")
    private String kafkaProductGroup;

    @Value("${service.kafkaProductDeleteGroup}")
    private String kafkaProductDeleteGroup;

    @Value("${service.kafkaInventoryGroup}")
    private String kafkaInventoryGroup;

    @Value("${service.kafkaPriceGroup}")
    private String kafkaPriceGroup;

//    @Value("${service.kafkaFailureTopic}")
//    private String kafkaFailureTopic;


    @Value("${service.kafkaConcurrency}")
    private int kafkaConcurrency;

    @Value("${service.kafkaMaxRetryAttempts}")
    private int maxRetryAttempts;

    @Value("${service.kafkaRetryInterval}")
    private int retryInterval;


//    @Autowired
//    public void setKafkaProducer(KafkaProducer kafkaProducer) {
//        this.kafkaProducer = kafkaProducer;
//    }

    @Bean
    public RetryPolicy getRetryPolicy() {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(maxRetryAttempts);
        return simpleRetryPolicy;
    }

    @Bean
    public FixedBackOffPolicy getBackOffPolicy() {
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryInterval);
        return backOffPolicy;
    }

    @Bean
    public RetryTemplate getRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(getRetryPolicy());
        retryTemplate.setBackOffPolicy(getBackOffPolicy());
        return retryTemplate;
    }


    public ConsumerFactory<String, String> consumerFactory(String group) {
        LOGGER.info("Initializing kafka consumer");
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

//    public ErrorHandler errorHandler() {
//        //TODO alert and put in retry queue on inability to process message
//        LOGGER.error("Could not process this message");
////        kafkaProducer.sendMessage(kafkaFailureTopic, GlobalConstants.PID, );
//        return (e, consumerRecord) -> LOGGER.error("Could not process this message", e);
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> productKafkaListenerContainerFactory() {
        return buildNewFactory(kafkaProductGroup);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> priceKafkaListenerContainerFactory() {
        return buildNewFactory(kafkaPriceGroup);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> inventoryKafkaListenerContainerFactory() {
        return buildNewFactory(kafkaInventoryGroup);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> productDeleteKafkaListenerContainerFactory() {
        return buildNewFactory(kafkaProductDeleteGroup);
    }


    private ConcurrentKafkaListenerContainerFactory<String, String> buildNewFactory(String group) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(kafkaConcurrency);
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
//        factory.getContainerProperties().setErrorHandler(errorHandler());
        factory.setConsumerFactory(consumerFactory(group));
        factory.setRetryTemplate(getRetryTemplate());
        return factory;
    }
}