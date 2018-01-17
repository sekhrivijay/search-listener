package com.ftd.services.listener.search.bl.processor;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.product.api.domain.response.Categories;
import com.ftd.services.product.api.domain.response.Image;
import com.ftd.services.product.api.domain.response.Operational;
import com.ftd.services.product.api.domain.response.Product;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import com.ftd.services.product.api.domain.response.Seo;
import com.ftd.services.product.api.domain.response.SpecificCategory;
import com.ftd.services.product.api.domain.response.Taxonomy;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named("productDelegate")
public class ProductDelegate implements Delegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDelegate.class);

    private SolrDocumentUtil solrDocumentUtil;

    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }


    @Override
    public SolrInputDocument process(Context context, SolrInputDocument solrInputDocument) {

        ProductServiceResponse productServiceResponse = context.getProductServiceResponse();
        String siteId = context.getSiteId();
        if (productServiceResponse == null
                || productServiceResponse.getProducts() == null
                || productServiceResponse.getProducts().size() == 0) {
            LOGGER.info("Empty result ");
            throw new RuntimeException("Empty result . Cannot index this product");
        }

        Product product = productServiceResponse.getProducts().stream().findFirst().get();
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SITE_ID, context.getSiteId());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TYPE, context.getType());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.NAME, product.getName());

        Seo seo = product.getSeo();
        if (seo != null) {
            solrDocumentUtil.addField(solrInputDocument, GlobalConstants.DESCRIPTION, seo.getDescription());
            solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TITLE, seo.getTitle());
            String keywordsStr = seo.getKeywords();
            if (StringUtils.isNotEmpty(keywordsStr)) {
                List<String> keywordTokens = Arrays.asList(
                        StringUtils.splitPreserveAllTokens(keywordsStr, GlobalConstants.COMMA));
                solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SEARCH_KEYWORDS, keywordTokens);
            }
        }

        Categories categories = getCategories(product.getTaxonomy(), siteId);
        if (categories != null && categories.getCategories() != null) {
            solrDocumentUtil.addField(
                    solrInputDocument,
                    GlobalConstants.CATEGORIES,
                    categories.getCategories().stream()
                            .flatMap(collection -> collection.getCategory().stream())
                            .collect(Collectors.toList())
                            .stream()
                            .map(SpecificCategory::getName)
                            .collect(Collectors.toList())
            );
        }

        solrDocumentUtil.addField(solrInputDocument,
                GlobalConstants.IS_ACTIVE,
                getIsActive(product.getOperational(), siteId));

        if (product.getIdentity() != null) {
            solrDocumentUtil.addField(solrInputDocument,
                    GlobalConstants.SKU_ID,
                    product.getIdentity().getSkuId());
        }


        if (product.getAssets() != null
                && product.getAssets().getImages() != null) {
            List<Image> imageList = product.getAssets().getImages();
            solrDocumentUtil.addField(
                    solrInputDocument,
                    GlobalConstants.PRIMARY_IMAGE,
                    imageList.stream()
                            .filter(image -> GlobalConstants.PRIMARY.equals(image.getType()))
                            .map(Image::getUrl)
                            .findFirst()
                            .get());
            solrDocumentUtil.addField(
                    solrInputDocument,
                    GlobalConstants.SMALL_IMAGE,
                    imageList.stream()
                            .filter(image -> GlobalConstants.SMALL.equals(image.getType()))
                            .map(Image::getUrl)
                            .findFirst()
                            .get());
        }

        return solrInputDocument;
    }

    private boolean getIsActive(Operational operational, String siteId) {
        if (operational == null
                || operational.getSites() == null) {
            return false;
        }
        if (siteId.equals(GlobalConstants.PROFLOWERS)
                && operational.getSites().getPfc() != null) {
            return operational.getSites().getPfc().getIsActive();
        }
        if (siteId.equals(GlobalConstants.FTD)
                && operational.getSites().getFtd() != null) {
            return operational.getSites().getFtd().getIsActive();
        }
        return false;
    }

    private Categories getCategories(Taxonomy taxonomy, String siteId) {
        if (taxonomy == null
                || taxonomy.getSites() != null) {
            return null;
        }
        if (siteId.equals(GlobalConstants.PROFLOWERS)) {
            return taxonomy.getSites().getPfc();
        }
        return taxonomy.getSites().getFtd();
    }

}
