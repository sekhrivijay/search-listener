package com.ftd.services.listener.search.bl.broker.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.gcp.pubsub.outbound.PubSubMessageHandler;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(name = "service.pubsub.enabled")
public class PubSubProducerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubProducerImpl.class);

    @Value("${service.pubsub.failureTopic}")
    private String pubsubFailureTopic;


    @Bean
    @ServiceActivator(inputChannel = "pubsubOutputFailureChannel")
    public MessageHandler messageSender(PubSubOperations pubsubTemplate) {
        PubSubMessageHandler adapter =
                new PubSubMessageHandler(pubsubTemplate, pubsubFailureTopic);
        adapter.setPublishCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.info("There was an error sending the message to failure topic");
            }

            @Override
            public void onSuccess(String result) {
                LOGGER.info("Message was sent successfully to failure topic.");
            }
        });

        return adapter;
    }
}
