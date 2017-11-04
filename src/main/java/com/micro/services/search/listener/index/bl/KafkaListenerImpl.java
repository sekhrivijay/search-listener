package com.micro.services.search.listener.index.bl;

import com.micro.services.search.listener.index.bl.orchestraction.Orchestrator;
import com.micro.services.search.listener.index.config.GlobalConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;


@Named
public class KafkaListenerImpl {

    private static final Logger LOGGER = Logger.getLogger(KafkaListenerImpl.class);
    private Orchestrator productOrchestrator;
    private Orchestrator priceOrchestrator;
    private Orchestrator inventoryOrchestrator;

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

    @KafkaListener(topics = "product", group = "productGroup")
    public void listenProduct(ConsumerRecord<String, String> record) throws Exception {
        String key = record.key();
        LOGGER.info(key);
        if (StringUtils.isEmpty(key) || !key.equals(GlobalConstants.PID)) {
            return;
        }
        productOrchestrator.process(record.value());
    }

    @KafkaListener(topics = "price", group = "priceGroup")
    public void listenPrice(ConsumerRecord<String, String> record) throws Exception {
        String key = record.key();
        LOGGER.info(key);
        if (StringUtils.isEmpty(key) || !key.equals(GlobalConstants.PID)) {
            return;
        }
        priceOrchestrator.process(record.value());
    }

    @KafkaListener(topics = "inventory", group = "inventoryGroup")
    public void listenInventory(ConsumerRecord<String, String> record) throws Exception {
        String key = record.key();
        LOGGER.info(key);
        if (StringUtils.isEmpty(key) || !key.equals(GlobalConstants.PID)) {
            return;
        }
        inventoryOrchestrator.process(record.value());
    }


}
