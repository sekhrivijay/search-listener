package com.ftd.services.listener.search.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gcp.pubsub.AckMode;
import org.springframework.integration.gcp.pubsub.inbound.PubSubInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;

@Configuration
@ConditionalOnProperty(name = "service.pubsub.enabled")
public class PubSubConsumerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubConsumerConfig.class);

    @Value("${service.pubsub.productSubscription}")
    private String pubsubProductSubscription;

    @Value("${service.pubsub.productDeleteSubscription}")
    private String pubsubProductDeleteSubscription;


    @Bean("${service.pubsub.productChannelName}")
    public MessageChannel pubsubProductInputChannel() {
        return new DirectChannel();
    }

    @Bean("${service.pubsub.productDeleteChannelName}")
    public MessageChannel pubsubProductDeleteInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("${service.pubsub.productChannelName}") MessageChannel inputChannel,
            PubSubOperations pubSubTemplate) {
        return getPubSubInboundChannelAdapter(inputChannel, pubSubTemplate, pubsubProductSubscription);
    }


    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapterDelete(
            @Qualifier("${service.pubsub.productDeleteChannelName}") MessageChannel inputChannel,
            PubSubOperations pubSubTemplate) {
        return getPubSubInboundChannelAdapter(inputChannel, pubSubTemplate, pubsubProductDeleteSubscription);
    }



    private PubSubInboundChannelAdapter getPubSubInboundChannelAdapter(
            @Qualifier("${service.pubsub.productChannelName}") MessageChannel inputChannel,
            PubSubOperations pubSubTemplate,
            String subscription) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, subscription);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);
        LOGGER.info("inputChannel configured for product ");
        return adapter;
    }


}
