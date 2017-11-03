package com.micro.services.search.listener.index.bl;

import com.micro.services.search.listener.index.solr.SolrService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;


@Named
public class KafkaListenerImpl {

    private static final Logger LOGGER = Logger.getLogger(KafkaListenerImpl.class);

    @Inject
    private SolrService solrService;


    @KafkaListener(topics = "product")
    public void listen(ConsumerRecord<String, String> record) throws Exception {
        String key = record.key();
        LOGGER.info(record.key());
        if(StringUtils.isEmpty(key)) {
            return;
        }
        solrService.updateJson(record.value());
    }


}
