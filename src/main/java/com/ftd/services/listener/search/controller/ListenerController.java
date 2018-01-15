package com.ftd.services.listener.search.controller;

import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import com.ftd.services.listener.search.product.generated.ProductDocument;
import com.ftd.services.listener.search.bl.product.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.function.Consumer;

@RestController
public class ListenerController {

    private Consumer<String> productOrchestrator;
    private ProductService productService;

    @Inject
    @Named("productOrchestrator")
    public void setProductOrchestrator(Consumer<String> productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @Inject
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

//    @RequestMapping("/query")
//    public ProductWrapper search(String pid) {
//        productOrchestrator.process(pid);
//        return productService.getProduct(pid);
//    }


    @RequestMapping("/query")
    public ProductDocument search(String pid) {
        productOrchestrator.accept(pid);
        return productService.getProductDetail(pid);
    }

    @RequestMapping("/product")
    public ProductServiceResponse product(String pid) {
//        productOrchestrator.accept(pid);
        return productService.getProductDetail(pid, "");
    }
}
