package com.ftd.services.listener.search.bl.util;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.product.api.domain.response.AttributeValue;
import com.ftd.services.product.api.domain.response.Attributes;
import com.ftd.services.product.api.domain.response.Category;
import com.ftd.services.product.api.domain.response.Desc;
import com.ftd.services.product.api.domain.response.Image;
import com.ftd.services.product.api.domain.response.Operational;
import com.ftd.services.product.api.domain.response.Product;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import com.ftd.services.product.api.domain.response.Seo;
import com.ftd.services.product.api.domain.response.SpecificCategory;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.api.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductUtil.class);
    private SolrDocumentUtil solrDocumentUtil;

    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }

    public boolean getIsActive(Operational operational) {
        if (operational == null) {
            return false;
        }
        return operational.getIsActive();
    }




    public void validateResponse(Context context, ProductServiceResponse productServiceResponse) {
        if (productServiceResponse == null
                || productServiceResponse.getProducts() == null
                || productServiceResponse.getProducts().size() == 0) {
            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty result from product service");
        }
    }


    public void addCategories(Context context, SolrInputDocument solrInputDocument, Product product) {
//        String siteId = context.getSiteId();
        List<Category> categoryList = product.getCategories();
        if (categoryList == null) {
            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty categories from product service ");
        }
        solrDocumentUtil.addField(
                solrInputDocument,
                GlobalConstants.CATEGORIES,
                categoryList.stream()
                        .flatMap(collection -> collection.getCategory().stream())
                        .collect(Collectors.toList())
                        .stream()
                        .map(SpecificCategory::getName)
                        .collect(Collectors.toList())
        );
    }

    public void addSeo(Context context, SolrInputDocument solrInputDocument, Product product) {
        Seo seo = product.getSeo();
        if (seo == null) {
            return;
//            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty seo from product service ");
        }
//        LOGGER.info(context.toString());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TITLE, seo.getTitle());
        String keywordsStr = seo.getKeywords();
        if (StringUtils.isNotEmpty(keywordsStr)) {
            List<String> keywordTokens = Arrays.asList(
                    StringUtils.splitPreserveAllTokens(keywordsStr, GlobalConstants.COMMA));
            solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SEARCH_KEYWORDS, keywordTokens);
        }
    }

    public String getPrimaryImage(Product product) {
        if (product.getAssets() != null
                && product.getAssets().getImages() != null) {
            List<Image> imageList = product.getAssets().getImages();
            Optional<String> imageOptional = imageList.stream()
                    .filter(image -> GlobalConstants.PRIMARY.equals(image.getType()))
                    .map(Image::getUrl)
                    .findFirst();
            if (imageOptional.isPresent()) {
                return imageOptional.get();
            }

        }
        return StringUtils.EMPTY;
    }

    public void addImage(Context context, SolrInputDocument solrInputDocument, Product product) {
        LOGGER.debug("context " + context);
        if (product.getAssets() != null
                && product.getAssets().getImages() != null) {
            List<Image> imageList = product.getAssets().getImages();

            imageList.stream()
                    .filter(image -> GlobalConstants.PRIMARY.equals(image.getType()))
                    .map(Image::getUrl)
                    .findFirst()
                    .ifPresent(imageUrl -> solrDocumentUtil.addField(
                            solrInputDocument,
                            GlobalConstants.PRIMARY_IMAGE,
                            imageUrl));
            imageList.stream()
                    .filter(image -> GlobalConstants.SMALL.equals(image.getType()))
                    .map(Image::getUrl)
                    .findFirst()
                    .ifPresent(imageUrl -> solrDocumentUtil.addField(
                            solrInputDocument,
                            GlobalConstants.SMALL_IMAGE,
                            imageUrl));

        }
    }

    public void addDescription(Context context, SolrInputDocument solrInputDocument, Product product) {
        List<Desc> description = product.getDescription();
        if (description == null) {
            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty description from product service ");
        }
        description
                .stream()
                .filter(e -> GlobalConstants.LONG.equals(e.getType()))
                .map(Desc::getValue)
                .findFirst()
                .ifPresent(desc -> solrDocumentUtil.addField(solrInputDocument, GlobalConstants.DESCRIPTION, desc));
    }


    public void addAttributes(Context context, SolrInputDocument solrInputDocument, Product product) {
        addAttributes(context, solrInputDocument, product, null);
    }

    public void addAttributes(Context context,
                              SolrInputDocument solrInputDocument,
                              Product product,
                              List<String> attributeNames) {
        List<Attributes> attributesList = product.getProductAttributes();
        if (attributesList == null) {
            return;
        }
        LOGGER.info("Adding attributes .... " + context);
        attributesList
                .stream()
                .filter(e -> filterAttribute(attributeNames, e))
                .forEach(attributes ->
                solrDocumentUtil.addField(solrInputDocument,
                        attributes.getName() + GlobalConstants.UNDERSCORE_A,
                        attributes.getValues()
                                .stream()
                                .map(AttributeValue::getValue)
                                .collect(Collectors.toList())));

    }

    private boolean filterAttribute(List<String> attributeNames, Attributes e) {
        return attributeNames == null
                || attributeNames.size() == 0
                || attributeNames.contains(e.getName());
    }

}

