package com.micro.services.search.listener.index.bl;

import com.micro.services.search.listener.index.bl.processor.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
public class DelegateInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegateInitializer.class);
    private Delegate productDelegate;
    private Delegate bsoDelegate;

    private List<Delegate> delegateList;

    public DelegateInitializer() {
        delegateList = new ArrayList<>();
    }

    @Inject
    @Named("productDelegate")
    public void setProductDelegate(Delegate productDelegate) {
        this.productDelegate = productDelegate;
    }

    @Inject
    @Named("bsoDelegate")
    public void setBsoDelegate(Delegate bsoDelegate) {
        this.bsoDelegate = bsoDelegate;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing delegate list ");
        delegateList.add(productDelegate);
        delegateList.add(bsoDelegate);
    }


    public List<Delegate> getDelegateList() {
        return delegateList;
    }
}
