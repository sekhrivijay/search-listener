package com.ftd.services.listener.search.bl.orchestraction;

import com.ftd.services.listener.search.bl.DelegateInitializer;
import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.function.Consumer;

@Named("productOrchestrator")
public class ProductOrchestrator extends BaseProductOrchestrator implements Consumer<Context> {

    public ProductOrchestrator(@Autowired EnhancedSolrClient enhancedSolrClient,
                               @Autowired DelegateInitializer delegateInitializer) {
        super(enhancedSolrClient, delegateInitializer);
    }

//    @Override
//    public void accept(Context context) {
//        super.accept(context);
//    }

}
