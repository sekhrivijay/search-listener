package com.ftd.services.listener.search.bl.orchestraction;

import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.function.Consumer;

@Named("productDeleteOrchestrator")
public class ProductDeleteOrchestrator implements Consumer<String> {

    private EnhancedSolrClient enhancedSolrClient;

    @Autowired
    public void setEnhancedSolrClient(EnhancedSolrClient enhancedSolrClient) {
        this.enhancedSolrClient = enhancedSolrClient;
    }

    @Override
    public void accept(String pid) {
        enhancedSolrClient.deleteById(pid);
    }
}
