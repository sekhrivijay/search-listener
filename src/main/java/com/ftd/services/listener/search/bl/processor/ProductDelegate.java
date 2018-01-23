package com.ftd.services.listener.search.bl.processor;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.util.BuilderUtil;
import com.ftd.services.listener.search.bl.util.ProductUtil;
import com.ftd.services.product.api.domain.response.Product;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import com.ftd.services.search.bl.clients.product.ProductClient;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;


@Named("productDelegate")
public class ProductDelegate implements Delegate {
//    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDelegate.class);

    private ProductClient productClient;
    private SolrDocumentUtil solrDocumentUtil;
    private BuilderUtil builderUtil;
    private ProductUtil productUtil;

    public ProductDelegate(@Autowired ProductClient productClient,
                           @Autowired SolrDocumentUtil solrDocumentUtil,
                           @Autowired BuilderUtil builderUtil,
                           @Autowired ProductUtil productUtil) {
        this.productClient = productClient;
        this.solrDocumentUtil = solrDocumentUtil;
        this.builderUtil = builderUtil;
        this.productUtil = productUtil;
    }


    @Override
    public SolrInputDocument process(Context context, SolrInputDocument solrInputDocument) {
        ProductServiceResponse productServiceResponse =
                productClient.callProductService(
                        builderUtil.buildSearchServiceRequest(context),
                        builderUtil.builcSearchServiceResponse(context));

        context.setProductServiceResponse(productServiceResponse);
        productUtil.validateResponse(context, productServiceResponse);
        productServiceResponse.getProducts()
                .stream()
                .findFirst()
                .ifPresent(product -> buildSolrDocument(context, solrInputDocument, product));

        return solrInputDocument;
    }


    private void buildSolrDocument(Context context, SolrInputDocument solrInputDocument, Product product) {
        String siteId = context.getSiteId();
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.ID, context.getSiteId() + product.getId());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.PID, product.getId());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SITE_ID, context.getSiteId());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TYPE, context.getType());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.NAME, product.getName());


        productUtil.addDescription(context, solrInputDocument, product);

        productUtil.addSeo(context, solrInputDocument, product);

        productUtil.addCategories(context, solrInputDocument, product);


        solrDocumentUtil.addField(solrInputDocument,
                GlobalConstants.IS_ACTIVE,
                productUtil.getIsActive(product.getOperational(), siteId));

        if (product.getIdentity() != null) {
            solrDocumentUtil.addField(solrInputDocument,
                    GlobalConstants.SKU_ID,
                    product.getIdentity().getSkuId());
        }

//
//        Attributes attributes = product.getProductAttributes();
//        if (attributes != null ) {
//            attributes.
//        }


        productUtil.addImage(context, solrInputDocument, product);
    }


}
