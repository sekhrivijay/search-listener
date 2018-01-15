package com.ftd.services.listener.search.bl.product.impl;

import com.ftd.services.listener.search.bl.product.ProductService;
import com.ftd.services.listener.search.product.generated.ProductDocument;
import com.ftd.services.listener.search.product.generated.ProductWrapper;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Named;

@Named("productService")
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);


    @Value("${service.productService.baseUrl}")
    private String productServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public ProductWrapper getProduct(String pid) {
        return null;
    }

    @Override
    public ProductDocument getProductDetail(String pid) {

        return null;
    }

    @Override
    public ProductServiceResponse getProductDetail(String pid, String siteId) {

        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(productServiceBaseUrl)
                .path(pid);
        LOGGER.info("Calling product service ");
        ResponseEntity<ProductServiceResponse> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                request,
                ProductServiceResponse.class);
        ProductServiceResponse productDocument = response.getBody();
        LOGGER.info(productDocument.toString());
        return productDocument;
    }


}
