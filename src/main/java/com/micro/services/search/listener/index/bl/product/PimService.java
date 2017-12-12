package com.micro.services.search.listener.index.bl.product;

import com.micro.services.product.generated.ProductWrapper;
import com.micro.services.product.generated.ProductDocument;

public interface PimService {
    ProductWrapper getProduct(String pid);
    ProductDocument getProductDetail(String pid);
}
