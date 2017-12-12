package com.micro.services.search.listener.index.bl.kafka;

import com.micro.services.search.listener.index.bl.orchestraction.Orchestrator;
import com.micro.services.search.listener.index.config.GlobalConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;


@Named
public class KafkaConsumerImpl implements KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerImpl.class);
    private Orchestrator productOrchestrator;
    private Orchestrator priceOrchestrator;
    private Orchestrator inventoryOrchestrator;
    private KafkaProducer kafkaProducer;

    @Value("${service.kafkaFailureTopic}")
    private String kafkaFailureTopic;

    @Inject
    @Named("productOrchestrator")
    public void setProductOrchestrator(Orchestrator productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @Inject
    @Named("priceOrchestrator")
    public void setPriceOrchestrator(Orchestrator priceOrchestrator) {
        this.priceOrchestrator = priceOrchestrator;
    }

    @Inject
    @Named("inventoryOrchestrator")
    public void setInventoryOrchestrator(Orchestrator inventoryOrchestrator) {
        this.inventoryOrchestrator = inventoryOrchestrator;
    }

    @Inject
    @Named("kafkaProducer")
    public void setKafkaProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Bean
    public KafkaListenerErrorHandler errorHandler() {
        return (message, e) -> {
            LOGGER.error("Sending data to failure queue", e);
            kafkaProducer.sendMessage(kafkaFailureTopic, GlobalConstants.PID, message.getPayload().toString());
            return null;
        };
    }

    @Override
    @KafkaListener(topics = "${service.kafkaProductTopics}",
            groupId = "${service.kafkaProductGroup}",
            containerFactory = "productKafkaListenerContainerFactory",
            errorHandler = "errorHandler")
    public void listenProduct(ConsumerRecord<String, String> record) throws Exception {
        if (!valid(record)) {
            return;
        }
        productOrchestrator.process(record.value());
    }


    @Override
    @KafkaListener(topics = "${service.kafkaPriceTopics}",
            groupId = "${service.kafkaPriceGroup}",
            containerFactory = "priceKafkaListenerContainerFactory",
            errorHandler = "errorHandler")
    public void listenPrice(ConsumerRecord<String, String> record) throws Exception {
        if (!valid(record)) {
            return;
        }
        priceOrchestrator.process(record.value());
    }

    @Override
    @KafkaListener(topics = "${service.kafkaInventoryTopics}",
            groupId = "${service.kafkaInventoryGroup}",
            containerFactory = "inventoryKafkaListenerContainerFactory",
            errorHandler = "errorHandler")
    public void listenInventory(ConsumerRecord<String, String> record) throws Exception {
        if (!valid(record)) {
            return;
        }
        inventoryOrchestrator.process(record.value());
    }

    private boolean valid(ConsumerRecord<String, String> record) {
        String key = record.key();
        LOGGER.info("key from message is " + key);
        if (StringUtils.isEmpty(key) || !key.equals(GlobalConstants.PID)) {
            return false;
        }
        return true;
    }


}
