package com.ftd.services.listener.search.bl.dm;

import com.ftd.services.listener.search.product.generated.ProductDocument;
import com.ftd.services.product.api.domain.response.Product;

public class Context {
    private String pid;
    private String siteId = "ftd";
    private String type = "default";
    private Product product;
    private ProductDocument productDocument;

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


    public static final class ContextBuilder {
        private String pid;
        private String siteId = "ftd";
        private String type = "default";
        private Product product;
        private ProductDocument productDocument;

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

        public ContextBuilder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public ContextBuilder withProductDocument(ProductDocument productDocument) {
            this.productDocument = productDocument;
            return this;
        }

        public Context build() {
            Context context = new Context();
            context.setPid(pid);
            context.setSiteId(siteId);
            context.setType(type);
            context.setProduct(product);
            context.setProductDocument(productDocument);
            return context;
        }
    }
}
