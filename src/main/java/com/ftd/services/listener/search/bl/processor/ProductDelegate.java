package com.ftd.services.listener.search.bl.processor;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.solr.SolrDocumentUtil;
import com.ftd.services.product.api.domain.response.Product;
import com.ftd.services.product.api.domain.response.Seo;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;

@Named("productDelegate")
public class ProductDelegate implements Delegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApolloProductDelegate.class);

    private SolrDocumentUtil solrDocumentUtil;

    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }


    @Override
    public SolrInputDocument process(Context context, SolrInputDocument solrInputDocument) {

        Product product = context.getProduct();
        if (product == null) {
            LOGGER.info("Empty result ");
            throw new RuntimeException("Empty result . Cannot index this product");
        }

        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SITE_ID, context.getSiteId());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TYPE, context.getType());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.NAME, product.getName());

        Seo seo = product.getSeo();
        if (seo != null) {
            solrDocumentUtil.addField(solrInputDocument, GlobalConstants.DESCRIPTION, seo.getDescription());
            solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TITLE, seo.getTitle());
            String keywordsStr = seo.getKeywords();
            if (StringUtils.isNotEmpty(keywordsStr)) {
                String[] keywordTokens = StringUtils.splitPreserveAllTokens(keywordsStr, GlobalConstants.COMMA);

            }
            solrDocumentUtil.addField(solrInputDocument, GlobalConstants.KEYWORDS, seo.getKeywords());
        }


        return solrInputDocument;
    }
}
