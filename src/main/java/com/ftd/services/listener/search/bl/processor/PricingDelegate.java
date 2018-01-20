package com.ftd.services.listener.search.bl.processor;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.util.BuilderUtil;
import com.ftd.services.listener.search.bl.util.MiscUtil;
import com.ftd.services.pricing.api.domain.response.FinalPrice;
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
                .findFirst()
                .ifPresent(pricing -> buildSolrDocument(context, solrInputDocument, pricing));


        return solrInputDocument;
    }

    private void buildSolrDocument(Context context, SolrInputDocument solrInputDocument, Pricing pricing) {
        Prices prices = pricing.getPrices();
        if (prices == null) {
            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty prices from pricing service");
        }
        FinalPrice finalPrice = prices.getFinalPrice();
        if (finalPrice == null) {
            MiscUtil.throwCommonValidationException(LOGGER, context, "Empty finalPrice from pricing service");
        }

        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.FINAL_PRICE, finalPrice.getValue());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.REGULAR_PRICE, prices.getRegularPrice());
    }
}
