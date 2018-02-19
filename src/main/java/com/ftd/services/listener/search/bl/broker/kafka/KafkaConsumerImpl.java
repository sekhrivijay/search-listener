package com.ftd.services.listener.search.bl.broker.kafka;

import com.ftd.services.search.api.GlobalConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.function.Consumer;


@Named
@ConditionalOnProperty(name = "service.kafka.enabled")
public class KafkaConsumerImpl implements KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerImpl.class);
    private Consumer<String> productOrchestrator;
    private Consumer<String> productDeleteOrchestrator;
    private Consumer<String> priceOrchestrator;
    private Consumer<String> inventoryOrchestrator;
//    private KafkaProducer kafkaProducer;

//    @Value("${service.kafka.failureTopic}")
//    private String kafkaFailureTopic;

    @Inject
    @Named("productOrchestrator")
    public void setProductOrchestrator(Consumer<String> productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @Inject
    @Named("priceOrchestrator")
    public void setPriceOrchestrator(Consumer<String> priceOrchestrator) {
        this.priceOrchestrator = priceOrchestrator;
    }

    @Inject
    @Named("inventoryOrchestrator")
    public void setInventoryOrchestrator(Consumer<String> inventoryOrchestrator) {
        this.inventoryOrchestrator = inventoryOrchestrator;
    }

    @Inject
    @Named("productDeleteOrchestrator")
    public void setProductDeleteOrchestrator(Consumer<String> productDeleteOrchestrator) {
        this.productDeleteOrchestrator = productDeleteOrchestrator;
    }

    //    @Inject
//    @Named("kafkaProducer")
//    public void setKafkaProducer(KafkaProducer kafkaProducer) {
//        this.kafkaProducer = kafkaProducer;
//    }

//    @Bean
//    public KafkaListenerErrorHandler errorHandler() {
//        return (message, e) -> {
//            LOGGER.error("Sending data to failure queue", e);
//            kafkaProducer.sendMessage(kafkaFailureTopic, GlobalConstants.PID,
//                    message.getPayload().toString() + ":" + e.getMessage());
//            return null;
//        };
//    }

    @Override
    @KafkaListener(topics = "${service.kafka.productTopics}",
            group = "${service.kafka.productGroup}",
            containerFactory = "productKafkaListenerContainerFactory")
    public void listenProduct(ConsumerRecord<String, String> record) throws Exception {
        processMessage(record, productOrchestrator);
    }

    @Override
    @KafkaListener(topics = "${service.kafka.productDeleteTopics}",
            group = "${service.kafka.productDeleteGroup}",
            containerFactory = "productDeleteKafkaListenerContainerFactory")
    public void listenProductDelete(ConsumerRecord<String, String> record) throws Exception {
        processMessage(record, productDeleteOrchestrator);
    }


    //    @Override
//    @KafkaListener(topics = "${service.kafka.priceTopics}",
//            groupId = "${service.kafka.priceGroup}",
//            containerFactory = "priceKafkaListenerContainerFactory",
//            errorHandler = "errorHandler")
    public void listenPrice(ConsumerRecord<String, String> record) throws Exception {
        processMessage(record, priceOrchestrator);
    }

    //
//    @Override
//    @KafkaListener(topics = "${service.kafka.inventoryTopics}",
//            groupId = "${service.kafka.inventoryGroup}",
//            containerFactory = "inventoryKafkaListenerContainerFactory",
//            errorHandler = "errorHandler")
    public void listenInventory(ConsumerRecord<String, String> record) throws Exception {
        processMessage(record, inventoryOrchestrator);
    }

    private void processMessage(ConsumerRecord<String, String> record, Consumer<String> orchestrator) {
        if (!valid(record)) {
            throw new RuntimeException("Invalid input message ");
        }
        orchestrator.accept(record.value());
    }

    private boolean valid(ConsumerRecord<String, String> record) {
        String key = record.key();
        LOGGER.info("key from message is " + key);
        return !StringUtils.isEmpty(key) && key.equals(GlobalConstants.PID);
    }


}
