package com.ftd.services.listener.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = SolrAutoConfiguration.class)
public class IndexListenerApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndexListenerApplication.class, args);
    }

}
