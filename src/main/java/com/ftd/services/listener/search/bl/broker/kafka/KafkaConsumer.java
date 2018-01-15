package com.ftd.services.listener.search.bl.broker.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaConsumer {
    void listenProduct(ConsumerRecord<String, String> record) throws Exception;
    void listenProductDelete(ConsumerRecord<String, String> record) throws Exception;
    void listenPrice(ConsumerRecord<String, String> record) throws Exception;
    void listenInventory(ConsumerRecord<String, String> record) throws Exception;
}
