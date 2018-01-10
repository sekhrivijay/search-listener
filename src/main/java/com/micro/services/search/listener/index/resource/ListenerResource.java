package com.micro.services.search.listener.index.resource;

import com.micro.services.product.generated.ProductDocument;
import com.micro.services.search.listener.index.bl.product.PimService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.function.Consumer;

@RestController
public class ListenerResource {

    private Consumer<String> productOrchestrator;
    private PimService pimService;

    @Inject
    @Named("productOrchestrator")
    public void setProductOrchestrator(Consumer<String> productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @Inject
    public void setPimService(PimService pimService) {
        this.pimService = pimService;
    }

//    @RequestMapping("/query")
//    public ProductWrapper search(String pid) {
//        productOrchestrator.process(pid);
//        return pimService.getProduct(pid);
//    }


    @RequestMapping("/query")
    public ProductDocument search(String pid) {
        productOrchestrator.accept(pid);
        return pimService.getProductDetail(pid);
    }
}
