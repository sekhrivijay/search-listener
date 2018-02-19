package com.ftd.services.listener.search.config;

import com.ftd.services.search.bl.clients.price.PricingClient;
import com.ftd.services.search.bl.clients.price.PricingClientImpl;
import com.ftd.services.search.bl.clients.product.ProductClient;
import com.ftd.services.search.bl.clients.product.ProductClientImpl;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClientImpl;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.bl.clients.solr.util.SolrUtil;
import com.ftd.services.search.api.GlobalConstants;
import com.google.gson.Gson;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(AppConfigProperties.class)
public class AppConfig {

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
    public PricingClient pricingClient(
            @Autowired RestTemplate restTemplate,
            @Value("${service.pricingService.baseUrl}") String baseUrl) {
        return new PricingClientImpl(restTemplate, baseUrl);
    }

    @Bean
    public SolrUtil solrUtil() {
        return new SolrUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public SolrClient solrClient(@Value("${service.solrService.zkEnsembleDestination}") String zkEnsembleDestination,
                                 @Value("${service.solrService.collectionDestination}") String collectionDestination,
                                 @Value("${service.solrService.zkTimeoutDestination}") int zkTimeoutDestination) {
        return new com.ftd.services.search.config.AppConfig()
                .solrClient(zkEnsembleDestination, collectionDestination, zkTimeoutDestination);
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


    @Value("${spring.application.name}")
    public void setApplicationName(String applicationName) {
        GlobalConstants.setApplicationName(applicationName);
    }

    @Value("${spring.profiles.active}")
    public void setEnvironment(String environment) {
        GlobalConstants.setEnvironment(environment);
    }

}
