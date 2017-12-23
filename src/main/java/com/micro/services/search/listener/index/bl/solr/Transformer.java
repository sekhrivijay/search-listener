package com.micro.services.search.listener.index.bl.solr;

import com.micro.services.product.generated.Product;
import com.micro.services.product.generated.ProductWrapper;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import com.micro.services.product.generated.ProductColors;
//import com.micro.services.product.generated.ProductDetails;
//import com.micro.services.product.generated.ProductDiscounts;
//import com.micro.services.product.generated.ProductDocument;
//import com.micro.services.product.generated.ProductUpsells;
//import com.micro.services.product.generated.Record_;
//import com.micro.services.product.generated.Result;
//import com.micro.services.product.generated.SourceCode;
//import com.micro.services.product.generated.SourceCodes;

//import java.util.List;
//import java.util.stream.Collectors;

//import com.micro.services.product.generated.Record__;

@Component
public class Transformer {

//    private static final Logger LOGGER = LoggerFactory.getLogger(Transformer.class);
    private SolrDocumentUtil solrDocumentUtil;

//    private Gson gson;
//
//    @Autowired
//    public void setGson(Gson gson) {
//        this.gson = gson;
//    }

    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }


//    public SolrInputDocument transform(ProductDocument productDocument) {
//        SolrInputDocument solrInputDocument = new SolrInputDocument();
//        Result result = productDocument.getResult();
//        if (result == null
//                || result.getProductDetails() == null
//                || result.getProductDetails().getRecord() == null) {
//            LOGGER.info("Empty result ");
//            throw new RuntimeException("Empty result . Cannot index this product");
//        }
//
//        ProductDetails productDetails = result.getProductDetails();
//        Record_ productDetailsRecord = productDetails.getRecord();
//        solrDocumentUtil.addField(solrInputDocument, "add_on_free_id", productDetailsRecord.getAddOnFreeId());
//        solrDocumentUtil.addField(solrInputDocument, "deluxe_price", productDetailsRecord.getDeluxePrice());
//        solrDocumentUtil.addField(solrInputDocument, "short_description", productDetailsRecord.getShortDescription());
//        solrDocumentUtil.addField(solrInputDocument, "allow_free_shipping_flag",
//                productDetailsRecord.getAllowFreeShippingFlag());
//        solrDocumentUtil.addField(solrInputDocument, "novator_name", productDetailsRecord.getNovatorName());
//        solrDocumentUtil.addField(solrInputDocument, "exception_start_date",
//                productDetailsRecord.getExceptionStartDate());
//        solrDocumentUtil.addField(solrInputDocument, "no_tax_flag", productDetailsRecord.getNoTaxFlag());
//        solrDocumentUtil.addField(solrInputDocument, "shipping_key", productDetailsRecord.getShippingKey());
//        solrDocumentUtil.addField(solrInputDocument, "premium_price", productDetailsRecord.getPremiumPrice());
//        solrDocumentUtil.addField(solrInputDocument, "color_size_flag", productDetailsRecord.getColorSizeFlag());
//        solrDocumentUtil.addField(solrInputDocument, "exception_message",
//                productDetailsRecord.getExceptionMessage());
//        solrDocumentUtil.addField(solrInputDocument, "ship_method_carrier",
//                productDetailsRecord.getShipMethodCarrier());
//        solrDocumentUtil.addField(solrInputDocument, "variable_price_max",
// productDetailsRecord.getVariablePriceMax());
//        solrDocumentUtil.addField(solrInputDocument, "delivery_type", productDetailsRecord.getDeliveryType());
//        solrDocumentUtil.addField(solrInputDocument, "product_id", productDetailsRecord.getProductId());
//        solrDocumentUtil.addField(solrInputDocument, "id", productDetailsRecord.getProductId());
//        solrDocumentUtil.addField(solrInputDocument, "exception_code", productDetailsRecord.getExceptionCode());
//        solrDocumentUtil.addField(solrInputDocument, "exception_end_date",
// productDetailsRecord.getExceptionEndDate());
//        solrDocumentUtil.addField(solrInputDocument, "add_on_cards_flag", productDetailsRecord.getAddOnCardsFlag());
//        solrDocumentUtil.addField(solrInputDocument, "row", productDetailsRecord.getRow());
//        solrDocumentUtil.addField(solrInputDocument, "standard_price", productDetailsRecord.getStandardPrice());
//        solrDocumentUtil.addField(solrInputDocument, "largeimage", productDetailsRecord.getLargeimage());
//        solrDocumentUtil.addField(solrInputDocument, "second_choice", productDetailsRecord.getSecondChoice());
//        solrDocumentUtil.addField(solrInputDocument, "novator_id", productDetailsRecord.getNovatorId());
//        solrDocumentUtil.addField(solrInputDocument, "long_description", productDetailsRecord.getLongDescription());
//        solrDocumentUtil.addField(solrInputDocument, "personal_greeting_flag",
//                productDetailsRecord.getPersonalGreetingFlag());
//        solrDocumentUtil.addField(solrInputDocument, "product_name", productDetailsRecord.getProductName());
//        solrDocumentUtil.addField(solrInputDocument, "smallimage", productDetailsRecord.getSmallimage());
//        solrDocumentUtil.addField(solrInputDocument, "discount_allowed_flag",
//                productDetailsRecord.getDiscountAllowedFlag());
//        solrDocumentUtil.addField(solrInputDocument, "product_type", productDetailsRecord.getProductType());
//        solrDocumentUtil.addField(solrInputDocument, "popularity_order_cnt",
//                productDetailsRecord.getPopularityOrderCnt());
//        solrDocumentUtil.addField(solrInputDocument, "shipping_system",
// productDetailsRecord.getShippingSystem());
//        solrDocumentUtil.addField(solrInputDocument, "ship_method_florist",
//                productDetailsRecord.getShipMethodFlorist());
//        solrDocumentUtil.addField(solrInputDocument, "weboe_blocked", productDetailsRecord.getWeboeBlocked());
//        solrDocumentUtil.addField(solrInputDocument, "custom_flag", productDetailsRecord.getCustomFlag());
//        solrDocumentUtil.addField(solrInputDocument, "add_on_funeral_flag",
// productDetailsRecord.getAddOnFuneralFlag());
//        solrDocumentUtil.addField(solrInputDocument, "premier_collection_flag",
//                productDetailsRecord.getPremierCollectionFlag());
//        solrDocumentUtil.addField(solrInputDocument, "over_21", productDetailsRecord.getOver21());
//        solrDocumentUtil.addField(solrInputDocument, "status", productDetailsRecord.getStatus());
//
//
////        ProductAddons productAddons = result.getProductAddons();
////        if (productAddons != null
////                && productAddons.getRecord() != null) {
////            solrDocumentUtil.addField(solrInputDocument, "product_addons", gson.toJson(productAddons));
////
////        }
//        ProductDiscounts productDiscounts = result.getProductDiscounts();
//        if (productDiscounts != null
//                && productDiscounts.getRecord() != null) {
//            solrDocumentUtil.addField(solrInputDocument, "product_discounts", gson.toJson(productDiscounts));
//        }
//
//        ProductUpsells productUpsells = result.getProductUpsells();
//        if (productUpsells != null
//                && productUpsells.getRecord() != null) {
//            solrDocumentUtil.addField(solrInputDocument, "product_upsells", gson.toJson(productUpsells));
//        }
////        ProductVases productVases = result.getProductVases();
////        if (productVases != null
////                && productVases.getRecord() != null) {
////            solrDocumentUtil.addField(solrInputDocument, "product_vases", gson.toJson(productVases));
////        }
//        ProductColors productColors = result.getProductColors();
//        if (productColors != null
//                && productColors.getRecord() != null) {
//            solrDocumentUtil.addField(solrInputDocument, "product_colors", gson.toJson(productColors));
//        }
//
//        SourceCodes sourceCodes = result.getSourceCodes();
//        if (sourceCodes != null
//                && sourceCodes.getSourceCode() != null) {
////            solrDocumentUtil.addField(solrInputDocument, "source_codes", gson.toJson(sourceCodes));
//            List<Integer> sourceCodeIntList = sourceCodes.getSourceCode()
//                    .stream()
//                    .map(SourceCode::getCode)
//                    .collect(Collectors.toList());
//            solrDocumentUtil.addField(solrInputDocument, "source_codes", sourceCodeIntList);
//            sourceCodes.getSourceCode()
//                    .stream()
////                    .map(SourceCode::getDiscountedprice)
//                    .forEach(e -> solrDocumentUtil.addField(
//                            solrInputDocument,
//                            "source_code_price_" + e.getCode() + "_f",
//                            e.getDiscountedprice()));
//
//        }
//        return solrInputDocument;
//    }


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
        solrDocumentUtil.addField(solrInputDocument, "ProductAvailabilityDescription",
                product.getProductAvailabilityDescription());
        solrDocumentUtil.addField(solrInputDocument, "ProductId", product.getProductId());
        solrDocumentUtil.addField(solrInputDocument, "ProductMetadata", product.getProductMetadata());
//        solrDocumentUtil.addField(solrInputDocument, "RequiredAccessories", product.getRequiredAccessories());
        solrDocumentUtil.addField(solrInputDocument, "SEODescription", product.getSEODescription());
        solrDocumentUtil.addField(solrInputDocument, "ShortDescription", product.getShortDescription());
//        solrDocumentUtil.addField(solrInputDocument, "PersonalizationAccessories",
// product.getPersonalizationAccessories());
        solrDocumentUtil.addField(solrInputDocument, "IsActive", product.getIsActive());
        solrDocumentUtil.addField(solrInputDocument, "SkuOwner", product.getSkuOwner());
//        solrDocumentUtil.addField(solrInputDocument, "ProductAttributes", product.getProductAttributes());


        return solrInputDocument;
    }
}
