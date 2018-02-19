package com.ftd.services.listener.search.bl.broker.pubsub;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.dm.EventEntity;
import com.ftd.services.listener.search.config.PubSubProducerConfig;
import com.ftd.services.search.api.GlobalConstants;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gcp.pubsub.support.GcpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.function.Consumer;

@Component
@ConditionalOnProperty(name = "service.pubsub.enabled")
public class PubSubConsumerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubConsumerImpl.class);
    private Consumer<Context> productOrchestrator;
    private Consumer<Context> pricingOrchestrator;
    private Consumer<Context> productDeleteOrchestrator;

    private PubSubProducerConfig.PubSubOutboundGateway messagingGateway;
    private Gson gson;

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setMessagingGateway(PubSubProducerConfig.PubSubOutboundGateway messagingGateway) {
        this.messagingGateway = messagingGateway;
    }


    @Inject
    @Named("productOrchestrator")
    public void setProductOrchestrator(Consumer<Context> productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @Inject
    @Named("productDeleteOrchestrator")
    public void setProductDeleteOrchestrator(Consumer<Context> productDeleteOrchestrator) {
        this.productDeleteOrchestrator = productDeleteOrchestrator;
    }

    @Inject
    @Named("pricingOrchestrator")
    public void setPricingOrchestrator(Consumer<Context> pricingOrchestrator) {
        this.pricingOrchestrator = pricingOrchestrator;
    }

    @Bean
    @ServiceActivator(inputChannel = "${service.pubsub.productChannelName}")
    public MessageHandler messageReceiverProduct() {
        return message -> processMessage(message, productOrchestrator);
    }


    @Bean
    @ServiceActivator(inputChannel = "${service.pubsub.productDeleteChannelName}")
    public MessageHandler messageReceiverProductDelete() {
        return message -> processMessage(message, productDeleteOrchestrator);
    }

    @Bean
    @ServiceActivator(inputChannel = "${service.pubsub.pricingChannelName}")
    public MessageHandler messageReceiverPricing() {
        return message -> processMessage(message, pricingOrchestrator);
    }


    private void processMessage(Message<?> message, Consumer<Context> orchestrator) {
        LOGGER.info("Message arrived! Payload: " + message.<String>getPayload());
        AckReplyConsumer consumer = null;
        try {
            consumer = (AckReplyConsumer) message.getHeaders().get(GcpHeaders.ACKNOWLEDGEMENT);
            String inputMessage = String.valueOf(message.getPayload());
            if (!valid(inputMessage)) {
                throw new Exception("Not a valid message " + inputMessage);
            }

            EventEntity eventEntity = gson.fromJson(inputMessage, EventEntity.class);
            Context context = Context.ContextBuilder.aContext()
//                    .withPid(inputMessage)
                    .withPid(eventEntity.getId())
//                    .withSiteId(GlobalConstants.FTD)
                    .withSiteId(eventEntity.getSiteId())
                    .withEventEntity(eventEntity)
                    .build();
            orchestrator.accept(context);
        } catch (Exception e) {
            LOGGER.error("Error processing product update ", e);
            messagingGateway.sendToPubSub(message.getPayload() + GlobalConstants.COLON + e.getMessage());
        }
        if (consumer != null) {
            consumer.ack();
        }
    }


    private boolean valid(String message) {
        return StringUtils.isNotEmpty(message);
    }
}
