package com.ftd.services.listener.search.bl.orchestraction;

import com.ftd.services.listener.search.bl.DelegateInitializer;
import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.util.Arrays;

public class BaseProductOrchestrator {
    private EnhancedSolrClient enhancedSolrClient;
    private DelegateInitializer delegateInitializer;

    public BaseProductOrchestrator(EnhancedSolrClient enhancedSolrClient, DelegateInitializer delegateInitializer) {
        this.enhancedSolrClient = enhancedSolrClient;
        this.delegateInitializer = delegateInitializer;
    }

    public void accept(Context context) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();

        delegateInitializer.getDelegateList()
                .forEach(e -> e.process(context, solrInputDocument));
        enhancedSolrClient.updateDocs(Arrays.asList(solrInputDocument));
    }


    public EnhancedSolrClient getEnhancedSolrClient() {
        return enhancedSolrClient;
    }

    public DelegateInitializer getDelegateInitializer() {
        return delegateInitializer;
    }

    public void setEnhancedSolrClient(EnhancedSolrClient enhancedSolrClient) {
        this.enhancedSolrClient = enhancedSolrClient;
    }

    public void setDelegateInitializer(DelegateInitializer delegateInitializer) {
        this.delegateInitializer = delegateInitializer;
    }
}
