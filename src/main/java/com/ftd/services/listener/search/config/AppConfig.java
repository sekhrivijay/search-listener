package com.ftd.services.listener.search.config;

import com.ftd.services.search.bl.clients.product.ProductClient;
import com.ftd.services.search.bl.clients.product.ProductClientImpl;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClientImpl;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.bl.clients.solr.util.SolrUtil;
import com.google.gson.Gson;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "service")
@EnableConfigurationProperties
@Configuration
public class AppConfig {

    private Map<String, String> sitesBsoMap;
    private Map<String, String> sitesAutofillKeywordMap;

    public AppConfig() {
        sitesBsoMap = new HashMap<>();
        sitesAutofillKeywordMap = new HashMap<>();
    }

    public Map<String, String> getSitesBsoMap() {
        return sitesBsoMap;
    }

    public void setSitesBsoMap(Map<String, String> sitesBsoMap) {
        this.sitesBsoMap = sitesBsoMap;
    }

    public Map<String, String> getSitesAutofillKeywordMap() {
        return sitesAutofillKeywordMap;
    }

    public void setSitesAutofillKeywordMap(Map<String, String> sitesAutofillKeywordMap) {
        this.sitesAutofillKeywordMap = sitesAutofillKeywordMap;
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ProductClient productClient(
            @Autowired RestTemplate restTemplate,
            @Value("${service.productService.baseUrl}") String baseUrl) {
        return new ProductClientImpl(restTemplate, baseUrl);
    }

    @Bean
    public SolrUtil solrUtil() {
        return new SolrUtil();
    }


    @Bean
    public EnhancedSolrClient enhancedSolrClient(
            @Autowired SolrClient solrClient,
            @Autowired SolrUtil solrUtil) {
        return new EnhancedSolrClientImpl(solrClient, solrUtil);
    }

    @Bean
    public SolrDocumentUtil solrDocumentUtil() {
        return new SolrDocumentUtil();
    }

    @Bean
    public SolrClient solrClient(
            @Value("${service.solrService.zkEnsembleDestination}") String zkEnsembleDestination,
            @Value("${service.solrService.collectionDestination}") String collectionDestination,
            @Value("${service.solrService.zkTimeoutDestination}") int zkTimeoutDestination
            ) {
        CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder()
                .withZkHost(zkEnsembleDestination)
                .build();
        cloudSolrClient.setDefaultCollection(collectionDestination);
        cloudSolrClient.setZkConnectTimeout(zkTimeoutDestination);

        return cloudSolrClient;
    }


}
