package com.ftd.services.listener.search.bl.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;

@Named
public class SolrServerInitializer {
    private SolrClient solrClient;


    @Value("${service.zkEnsembleDestination}")
    private String zkEnsembleDestination;

    @Value("${service.collectionDestination}")
    private String collectionDestination;

    @Value("${service.zkTimeoutDestination}")
    private int zkTimeoutDestination;

    SolrServerInitializer() {
    }

    @Bean
    public SolrClient getSolrClient() {
        CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder()
                .withZkHost(zkEnsembleDestination)
                .build();
        cloudSolrClient.setDefaultCollection(collectionDestination);
        cloudSolrClient.setZkConnectTimeout(zkTimeoutDestination);

        this.solrClient = cloudSolrClient;


        return solrClient;
    }
}