package com.micro.services.search.listener.index.bl.solr;

import com.micro.services.product.generated.Product;
import com.micro.services.product.generated.ProductWrapper;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Transformer {

    private SolrDocumentUtil solrDocumentUtil;

    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }


    public SolrInputDocument transform(ProductWrapper productWrapper) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        Product product = productWrapper.getProduct();
        solrDocumentUtil.addField(solrInputDocument, "ImageUrl", product.getImageUrl());
        solrDocumentUtil.addField(solrInputDocument, "Name", product.getName());
        solrDocumentUtil.addField(solrInputDocument, "ProductCode", product.getProductCode());
        solrDocumentUtil.addField(solrInputDocument, "SalePrice", product.getSalePrice());
        solrDocumentUtil.addField(solrInputDocument, "SkuId", product.getSkuId());
        solrDocumentUtil.addField(solrInputDocument, "id", product.getProductId());
        solrDocumentUtil.addField(solrInputDocument, "SkuTypeId", product.getSkuTypeId());
        solrDocumentUtil.addField(solrInputDocument, "StrikePrice", product.getStrikePrice());
        solrDocumentUtil.addField(solrInputDocument, "SmallImageUrl", product.getSmallImageUrl());
//        solrDocumentUtil.addField(solrInputDocument, "Accessories", product.getAccessories());
        solrDocumentUtil.addField(solrInputDocument, "BulletedDescription", product.getBulletedDescription());
//        solrDocumentUtil.addField(solrInputDocument, "BundledAccessories", product.getBundledAccessories());
        solrDocumentUtil.addField(solrInputDocument, "Description", product.getDescription());
        solrDocumentUtil.addField(solrInputDocument, "DisplayInSearchResults", product.getDisplayInSearchResults());
        solrDocumentUtil.addField(solrInputDocument, "IsNew", product.getIsNew());
        solrDocumentUtil.addField(solrInputDocument, "Keywords", product.getKeywords());
        solrDocumentUtil.addField(solrInputDocument, "ListedDescription", product.getListedDescription());
        solrDocumentUtil.addField(solrInputDocument, "LongDescription", product.getLongDescription());
        solrDocumentUtil.addField(solrInputDocument, "MaxTimeInTransit", product.getMaxTimeInTransit());
        solrDocumentUtil.addField(solrInputDocument, "MetaDescription", product.getMetaDescription());
        solrDocumentUtil.addField(solrInputDocument, "ProductAvailabilityDescription", product.getProductAvailabilityDescription());
        solrDocumentUtil.addField(solrInputDocument, "ProductId", product.getProductId());
        solrDocumentUtil.addField(solrInputDocument, "ProductMetadata", product.getProductMetadata());
//        solrDocumentUtil.addField(solrInputDocument, "RequiredAccessories", product.getRequiredAccessories());
        solrDocumentUtil.addField(solrInputDocument, "SEODescription", product.getSEODescription());
        solrDocumentUtil.addField(solrInputDocument, "ShortDescription", product.getShortDescription());
//        solrDocumentUtil.addField(solrInputDocument, "PersonalizationAccessories", product.getPersonalizationAccessories());
        solrDocumentUtil.addField(solrInputDocument, "IsActive", product.getIsActive());
        solrDocumentUtil.addField(solrInputDocument, "SkuOwner", product.getSkuOwner());
//        solrDocumentUtil.addField(solrInputDocument, "ProductAttributes", product.getProductAttributes());


        return solrInputDocument;
    }
}
