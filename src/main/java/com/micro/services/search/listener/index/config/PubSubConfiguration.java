package com.micro.services.search.listener.index.config;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
//import com.google.cloud.pubsub.v1.AckReplyConsumer;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
//import org.springframework.cloud.gcp.pubsub.support.GcpHeaders;
//import org.springframework.context.annotation.Bean;
//import org.springframework.integration.annotation.MessagingGateway;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.gcp.pubsub.AckMode;
//import org.springframework.integration.gcp.pubsub.inbound.PubSubInboundChannelAdapter;
//import org.springframework.integration.gcp.pubsub.outbound.PubSubMessageHandler;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.util.concurrent.ListenableFutureCallback;

@Configuration
public class PubSubConfiguration {
//    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubConfiguration.class);

//    @Bean
//    public MessageChannel pubsubInputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public PubSubInboundChannelAdapter messageChannelAdapter(
//            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
//            PubSubOperations pubSubTemplate) {
//        PubSubInboundChannelAdapter adapter =
//                new PubSubInboundChannelAdapter(pubSubTemplate, "testSubscription");
//        adapter.setOutputChannel(inputChannel);
//        adapter.setAckMode(AckMode.MANUAL);
//        return adapter;
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "pubsubInputChannel")
//    public MessageHandler messageReceiver() {
//        return message -> {
//            LOGGER.info("Message arrived! Payload: " + message.getPayload());
//            AckReplyConsumer consumer =
//                    (AckReplyConsumer) message.getHeaders().get(GcpHeaders.ACKNOWLEDGEMENT);
//            consumer.ack();
//        };
//    }
//
//
//    @Bean
//    @ServiceActivator(inputChannel = "pubsubOutputChannel")
//    public MessageHandler messageSender(PubSubOperations pubsubTemplate) {
//        PubSubMessageHandler adapter =
//                new PubSubMessageHandler(pubsubTemplate, "exampleTopic");
//        adapter.setPublishCallback(new ListenableFutureCallback<String>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                LOGGER.info("There was an error sending the message.");
//            }
//
//            @Override
//            public void onSuccess(String result) {
//                LOGGER.info("Message was sent successfully.");
//            }
//        });
//
//        return adapter;
//    }
//
//    @MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
//    public interface PubsubOutboundGateway {
//        void sendToPubsub(String text);
//    }

}
