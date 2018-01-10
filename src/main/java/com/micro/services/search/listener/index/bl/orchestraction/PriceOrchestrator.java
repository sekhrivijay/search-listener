package com.micro.services.search.listener.index.bl.orchestraction;

import javax.inject.Named;
import java.util.function.Consumer;

@Named("priceOrchestrator")
public class PriceOrchestrator implements Consumer<String> {
    @Override
    public void accept(String pid) {

    }
}
