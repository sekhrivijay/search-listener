package com.micro.services.search.listener.index.bl.broker.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;

import javax.inject.Named;

@Named("kafkaProducer")
@ConditionalOnProperty(name = "service.kafka.enabled")
public class KafkaProducerImpl implements KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }
}
