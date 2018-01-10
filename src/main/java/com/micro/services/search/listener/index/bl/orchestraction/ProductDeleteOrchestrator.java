package com.micro.services.search.listener.index.bl.orchestraction;

import com.micro.services.search.listener.index.bl.solr.SolrService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.function.Consumer;

@Named("productDeleteOrchestrator")
public class ProductDeleteOrchestrator implements Consumer<String> {

    private SolrService solrService;

    @Inject
    @Named("solrService")
    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
    }

    @Override
    public void accept(String pid) {
        solrService.deleteById(pid);
    }
}
