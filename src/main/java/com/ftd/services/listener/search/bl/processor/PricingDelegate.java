package com.ftd.services.listener.search.bl.processor;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.util.BuilderUtil;
import com.ftd.services.listener.search.bl.util.MiscUtil;
import com.ftd.services.pricing.api.domain.response.PriceTypes;
import com.ftd.services.pricing.api.domain.response.Prices;
import com.ftd.services.pricing.api.domain.response.Pricing;
import com.ftd.services.pricing.api.domain.response.PricingResponse;
import com.ftd.services.search.bl.clients.price.PricingClient;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.List;

@Named("pricingDelegate")
public class PricingDelegate implements Delegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricingDelegate.class);
    private PricingClient pricingClient;
    private SolrDocumentUtil solrDocumentUtil;
    private BuilderUtil builderUtil;

    public PricingDelegate(@Autowired PricingClient pricingClient,
                           @Autowired SolrDocumentUtil solrDocumentUtil,
                           @Autowired BuilderUtil builderUtil) {
        this.pricingClient = pricingClient;
        this.solrDocumentUtil = solrDocumentUtil;
        this.builderUtil = builderUtil;
    }

    @Override
    public SolrInputDocument process(Context context, SolrInputDocument solrInputDocument) {

        PricingResponse pricingResponse =
                pricingClient.callPriceService(
                        builderUtil.buildSearchServiceRequest(context),
                        builderUtil.builcSearchServiceResponse(context));

        context.setPricingResponse(pricingResponse);
        if (pricingResponse == null
                || pricingResponse.getPricing() == null
                || pricingResponse.getPricing().size() == 0) {
            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty result from pricing service ");
        }
        pricingResponse.getPricing()
                .stream()
                .filter(e -> e.getId() != null)
                .filter(e -> e.getId().equals(context.getPid()))
                .findFirst()
                .ifPresent(pricing -> buildSolrDocument(context, solrInputDocument, pricing));


        return solrInputDocument;
    }

    private void buildSolrDocument(Context context, SolrInputDocument solrInputDocument, Pricing pricing) {
        List<Prices> pricesList = pricing.getPrices();
        if (pricesList == null) {
            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty prices from pricing service");
        }

        pricesList
                .stream()
                .filter(e -> e.getType() == PriceTypes.regular)
                .filter(e -> e.getValue() != null)
                .findFirst()
                .ifPresent(e -> solrDocumentUtil.addField(solrInputDocument,
                        GlobalConstants.REGULAR_PRICE,
                        e.getValue().doubleValue()));


//        FinalPrice finalPrice = pricesList.getFinalPrice();
//        if (finalPrice == null) {
//            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty finalPrice from pricing service");
//        }
//
//        solrDocumentUtil.addField(solrInputDocument,
//                GlobalConstants.FINAL_PRICE,
//                finalPrice.getValue().doubleValue());
//        solrDocumentUtil.addField(solrInputDocument,
//                GlobalConstants.REGULAR_PRICE,
//                pricesList.getRegularPrice().doubleValue());
//    }
    }
}
