package com.ftd.services.listener.search.bl.product;

import com.ftd.services.listener.search.product.generated.ProductDocument;
import com.ftd.services.listener.search.product.generated.ProductWrapper;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;

public interface ProductService {
    ProductWrapper getProduct(String pid);
    ProductDocument getProductDetail(String pid);
    ProductServiceResponse getProductDetail(String pid, String siteId);
}
