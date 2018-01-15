package com.ftd.services.listener.search.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;

@Configuration
@ConditionalOnProperty(name = "service.pubsub.enabled")
public class PubSubProducerConfig {

    @MessagingGateway(defaultRequestChannel = "${service.pubsub.failureChannelName}")
    public interface PubSubOutboundGateway {
        void sendToPubSub(String text);
    }


}
