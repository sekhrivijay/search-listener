package com.micro.services.search.listener.index.bl.kafka;

public interface KafkaProducer {
    void sendMessage(String topic, String key, String message);
}
