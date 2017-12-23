package com.micro.services.search.listener.index.bl.orchestraction;

import com.micro.services.product.generated.ProductDocument;
import com.micro.services.search.listener.index.bl.DelegateInitializer;
import com.micro.services.search.listener.index.bl.dm.Context;
import com.micro.services.search.listener.index.bl.product.PimService;
import com.micro.services.search.listener.index.bl.solr.SolrService;
import org.apache.solr.common.SolrInputDocument;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Named("productOrchestrator")
public class ProductOrchestrator implements Orchestrator {
    private PimService pimService;
    private SolrService solrService;
    private DelegateInitializer delegateInitializer;

    @Inject
    public void setDelegateInitializer(DelegateInitializer delegateInitializer) {
        this.delegateInitializer = delegateInitializer;
    }

    @Inject
    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
    }

    @Inject
    public void setPimService(PimService pimService) {
        this.pimService = pimService;
    }

    @Override
    public void process(String pid) {
//        ProductWrapper productWrapper = pimService.getProduct(pid);
        ProductDocument productDetail = pimService.getProductDetail(pid);
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        Context context = Context.ContextBuilder.aContext()
                .withPid(pid)
                .withProductDocument(productDetail)
                .build();
        delegateInitializer.getDelegateList()
                .forEach(e -> e.process(context, solrInputDocument));
//        SolrInputDocument solrInputDocument = transformer.transform(productDetail);
        List<SolrInputDocument> solrInputDocumentList = Arrays.asList(solrInputDocument);
        solrService.updateDocs(solrInputDocumentList);
    }
}
