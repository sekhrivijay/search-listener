package com.ftd.services.listener.search.bl.orchestraction;

import com.ftd.services.listener.search.bl.DelegateInitializer;
import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.product.ProductService;
import com.ftd.services.listener.search.bl.solr.SolrService;
import com.ftd.services.listener.search.product.generated.ProductDocument;
import org.apache.solr.common.SolrInputDocument;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.function.Consumer;

@Named("productOrchestrator")
public class ProductOrchestrator implements Consumer<String> {
    private ProductService productService;
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
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }


    @Override
    public void accept(String pid) {
//        ProductWrapper productWrapper = productService.getProduct(pid);
        ProductDocument productDetail = productService.getProductDetail(pid);
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        Context context = Context.ContextBuilder.aContext()
                .withPid(pid)
                .withProductDocument(productDetail)
                .build();
        delegateInitializer.getDelegateList()
                .forEach(e -> e.process(context, solrInputDocument));
        solrService.updateDocs(Arrays.asList(solrInputDocument));
    }
}
