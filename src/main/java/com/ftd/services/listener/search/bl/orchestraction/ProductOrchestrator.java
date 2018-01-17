package com.ftd.services.listener.search.bl.orchestraction;

import com.ftd.services.listener.search.bl.DelegateInitializer;
import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import com.ftd.services.search.api.request.SearchServiceRequest;
import com.ftd.services.search.api.response.Document;
import com.ftd.services.search.api.response.SearchServiceResponse;
import com.ftd.services.search.bl.clients.product.ProductClient;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Named("productOrchestrator")
public class ProductOrchestrator implements Consumer<String> {
    private ProductClient productClient;
    private EnhancedSolrClient enhancedSolrClient;
    private DelegateInitializer delegateInitializer;

    @Inject
    public void setDelegateInitializer(DelegateInitializer delegateInitializer) {
        this.delegateInitializer = delegateInitializer;
    }

    @Autowired
    public void setEnhancedSolrClient(EnhancedSolrClient enhancedSolrClient) {
        this.enhancedSolrClient = enhancedSolrClient;
    }

    @Autowired
    public void setProductClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public void accept(String pid) {
//        ProductWrapper productWrapper = productService.getProduct(pid);
//        ProductDocument productDetail = productService.getProductDetail(pid);
        Document document = new Document();
        Map<String, Object> map = new HashMap<>();
        map.put(GlobalConstants.ID, pid);
        document.setRecord(map);
        SearchServiceRequest searchServiceRequest = SearchServiceRequest
                .SearchServiceRequestBuilder
                .aSearchServiceRequest()
                .withSiteId(GlobalConstants.PROFLOWERS)
                .build();
        SearchServiceResponse searchServiceResponse = SearchServiceResponse
                .SearchServiceResponseBuilder
                .aSearchServiceResponse()
                .withDocumentList(Arrays.asList(document))
                .build();
        ProductServiceResponse productServiceResponse = productClient
                .callProductService(searchServiceRequest, searchServiceResponse);
//        ProductServiceResponse productServiceResponse = productService.getProductDetail(pid, "proflowers");
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        Context context = Context.ContextBuilder.aContext()
                .withPid(pid)
                .withProductServiceResponse(productServiceResponse)
//                .withProductDocument(productDetail)
                .build();
        delegateInitializer.getDelegateList()
                .forEach(e -> e.process(context, solrInputDocument));
//        solrService.updateDocs(Arrays.asList(solrInputDocument));
        enhancedSolrClient.updateDocs(Arrays.asList(solrInputDocument));
    }
}
