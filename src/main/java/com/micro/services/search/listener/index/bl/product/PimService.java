package com.micro.services.search.listener.index.bl.product;

import com.micro.services.product.generated.ProductWrapper;

public interface PimService {
    ProductWrapper getProduct(String pid);
}
