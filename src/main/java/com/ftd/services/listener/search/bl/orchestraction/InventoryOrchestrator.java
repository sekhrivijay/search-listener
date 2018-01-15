package com.ftd.services.listener.search.bl.orchestraction;

import javax.inject.Named;
import java.util.function.Consumer;

@Named("inventoryOrchestrator")
public class InventoryOrchestrator implements Consumer<String> {
    @Override
    public void accept(String pid) {

    }
}
