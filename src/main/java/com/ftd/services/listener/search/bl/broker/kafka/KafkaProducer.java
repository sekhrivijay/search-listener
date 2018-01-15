package com.ftd.services.listener.search.bl.broker.kafka;

public interface KafkaProducer {
    void sendMessage(String topic, String key, String message);
}
