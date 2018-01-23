package com.ftd.services.listener.search.bl.dm;

import com.ftd.services.listener.search.product.generated.ProductDocument;
import com.ftd.services.pricing.api.domain.response.PricingResponse;
import com.ftd.services.product.api.domain.response.Product;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import com.ftd.services.search.config.GlobalConstants;

public class Context {
    private String pid;
    private String siteId = GlobalConstants.FTD;
    private String type = GlobalConstants.DEFAULT;
    private EventEntity eventEntity;
    private Product product;
    private ProductDocument productDocument;
    private PricingResponse pricingResponse;
    private ProductServiceResponse productServiceResponse;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public ProductDocument getProductDocument() {
        return productDocument;
    }

    public void setProductDocument(ProductDocument productDocument) {
        this.productDocument = productDocument;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductServiceResponse getProductServiceResponse() {
        return productServiceResponse;
    }

    public void setProductServiceResponse(ProductServiceResponse productServiceResponse) {
        this.productServiceResponse = productServiceResponse;
    }

    public PricingResponse getPricingResponse() {
        return pricingResponse;
    }

    public void setPricingResponse(PricingResponse pricingResponse) {
        this.pricingResponse = pricingResponse;
    }

    public EventEntity getEventEntity() {
        return eventEntity;
    }

    public void setEventEntity(EventEntity eventEntity) {
        this.eventEntity = eventEntity;
    }


    public static final class ContextBuilder {
        private String pid;
        private String siteId = GlobalConstants.FTD;
        private String type = GlobalConstants.DEFAULT;
        private EventEntity eventEntity;
        private Product product;
        private ProductDocument productDocument;
        private PricingResponse pricingResponse;
        private ProductServiceResponse productServiceResponse;

        private ContextBuilder() {
        }

        public static ContextBuilder aContext() {
            return new ContextBuilder();
        }

        public ContextBuilder withPid(String pid) {
            this.pid = pid;
            return this;
        }

        public ContextBuilder withSiteId(String siteId) {
            this.siteId = siteId;
            return this;
        }

        public ContextBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public ContextBuilder withEventEntity(EventEntity eventEntity) {
            this.eventEntity = eventEntity;
            return this;
        }

        public ContextBuilder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public ContextBuilder withProductDocument(ProductDocument productDocument) {
            this.productDocument = productDocument;
            return this;
        }

        public ContextBuilder withPricingResponse(PricingResponse pricingResponse) {
            this.pricingResponse = pricingResponse;
            return this;
        }

        public ContextBuilder withProductServiceResponse(ProductServiceResponse productServiceResponse) {
            this.productServiceResponse = productServiceResponse;
            return this;
        }

        public Context build() {
            Context context = new Context();
            context.setPid(pid);
            context.setSiteId(siteId);
            context.setType(type);
            context.setEventEntity(eventEntity);
            context.setProduct(product);
            context.setProductDocument(productDocument);
            context.setPricingResponse(pricingResponse);
            context.setProductServiceResponse(productServiceResponse);
            return context;
        }
    }
}
