package com.micro.services.search.listener.index.bl.orchestraction;

import com.micro.services.product.generated.ProductDocument;
import com.micro.services.search.listener.index.bl.product.PimService;
import com.micro.services.search.listener.index.bl.solr.SolrService;
import com.micro.services.search.listener.index.bl.solr.Transformer;
import com.micro.services.search.listener.index.bl.solr.impl.SolrServiceImpl;
import org.apache.solr.common.SolrInputDocument;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Named("productOrchestrator")
public class ProductOrchestrator implements Orchestrator {
    private PimService pimService;
    private Transformer transformer;
    private SolrService solrService;

    @Inject
    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
    }

    @Inject
    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    @Inject
    public void setPimService(PimService pimService) {
        this.pimService = pimService;
    }

    @Override
    public void process(String pid) {
//        ProductWrapper productWrapper = pimService.getProduct(pid);
        ProductDocument productDetail = pimService.getProductDetail(pid);
        SolrInputDocument solrInputDocument = transformer.transform(productDetail);
        List<SolrInputDocument> solrInputDocumentList = Arrays.asList(solrInputDocument);
        solrService.updateDocs(solrInputDocumentList);
    }
}
