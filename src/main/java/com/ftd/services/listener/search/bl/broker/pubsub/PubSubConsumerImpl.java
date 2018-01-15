package com.ftd.services.listener.search.bl.broker.pubsub;

import com.ftd.services.listener.search.config.PubSubProducerConfig;
import com.ftd.services.search.config.GlobalConstants;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
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
    private Consumer<String> productOrchestrator;
    private Consumer<String> productDeleteOrchestrator;

    private PubSubProducerConfig.PubSubOutboundGateway messagingGateway;

    @Autowired
    public void setMessagingGateway(PubSubProducerConfig.PubSubOutboundGateway messagingGateway) {
        this.messagingGateway = messagingGateway;
    }


    @Inject
    @Named("productOrchestrator")
    public void setProductOrchestrator(Consumer<String> productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @Inject
    @Named("productDeleteOrchestrator")
    public void setProductDeleteOrchestrator(Consumer<String> productDeleteOrchestrator) {
        this.productDeleteOrchestrator = productDeleteOrchestrator;
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


    private void processMessage(Message<?> message, Consumer<String> orchestrator) {
        LOGGER.info("Message arrived! Payload: " + message.<String>getPayload());
        AckReplyConsumer consumer = null;
        try {
            consumer = (AckReplyConsumer) message.getHeaders().get(GcpHeaders.ACKNOWLEDGEMENT);
            String inputMessage = String.valueOf(message.getPayload());
            if (!valid(inputMessage)) {
                throw new Exception("Not a valid message " + inputMessage);
            }
            orchestrator.accept(inputMessage);
        } catch (Exception e) {
            LOGGER.error("Error processing product update ");
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
