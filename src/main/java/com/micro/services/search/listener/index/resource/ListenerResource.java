package com.micro.services.search.listener.index.resource;

import com.micro.services.product.generated.ProductWrapper;
import com.micro.services.search.listener.index.bl.orchestraction.Orchestrator;
import com.micro.services.search.listener.index.bl.product.PimService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.inject.Named;

@RestController
public class ListenerResource {

    private Orchestrator productOrchestrator;
    private PimService pimService;

    @Inject
    @Named("productOrchestrator")
    public void setProductOrchestrator(Orchestrator productOrchestrator) {
        this.productOrchestrator = productOrchestrator;
    }

    @Inject
    public void setPimService(PimService pimService) {
        this.pimService = pimService;
    }

    @RequestMapping("/query")
    public ProductWrapper search(String pid) {
        productOrchestrator.process(pid);
        return pimService.getProduct(pid);
    }
}
