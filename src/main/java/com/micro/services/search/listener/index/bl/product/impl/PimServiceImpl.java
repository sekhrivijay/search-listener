package com.micro.services.search.listener.index.bl.product.impl;

import com.micro.services.product.generated.ProductWrapper;
import com.micro.services.product.generated.ProductDocument;
import com.micro.services.search.listener.index.bl.product.PimService;
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


@Named("pimService")
public class PimServiceImpl implements PimService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PimServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.pimService.baseUrl}")
    private String pimServiceBaseUrl;

    @Value("${service.pimService.apolloBaseUrl}")
    private String pimServiceApolloBaseUrl;

    @Value("${service.pimService.accessToken}")
    private String pimServiceAccessToken;

    @Override
    public ProductWrapper getProduct(String pid) {
        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(pimServiceBaseUrl)
                .queryParam("productId", pid)
                .queryParam("applicationToken", pimServiceAccessToken);
        LOGGER.info("Calling PIM  service ");
        ResponseEntity<ProductWrapper> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                request,
                ProductWrapper.class);
        ProductWrapper productWrapper = response.getBody();
        LOGGER.info(productWrapper.toString());
        return productWrapper;
    }

    @Override
    public ProductDocument getProductDetail(String pid) {
        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(pimServiceApolloBaseUrl)
                .queryParam("prodid", pid);
        LOGGER.info("Calling PIM  service ");
        ResponseEntity<ProductDocument> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                request,
                ProductDocument.class);
        ProductDocument productDocument = response.getBody();
        LOGGER.info(productDocument.toString());
        return productDocument;
    }
}
