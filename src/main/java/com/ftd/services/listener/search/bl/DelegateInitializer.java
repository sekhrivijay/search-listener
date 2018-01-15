package com.ftd.services.listener.search.bl;

import com.ftd.services.listener.search.bl.processor.Delegate;
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
    private Delegate apolloProductDelegate;
    private Delegate bsoDelegate;
//    private Delegate autofillDelegate;

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
    @Named("apolloProductDelegate")
    public void setApolloProductDelegate(Delegate apolloProductDelegate) {
        this.apolloProductDelegate = apolloProductDelegate;
    }


    @Inject
    @Named("bsoDelegate")
    public void setBsoDelegate(Delegate bsoDelegate) {
        this.bsoDelegate = bsoDelegate;
    }

//    @Inject
//    @Named("autofillDelegate")
//    public void setAutofillDelegate(Delegate autofillDelegate) {
//        this.autofillDelegate = autofillDelegate;
//    }

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing delegate list ");
        delegateList.add(apolloProductDelegate);
        delegateList.add(productDelegate);
        delegateList.add(bsoDelegate);
//        delegateList.add(autofillDelegate);
    }


    public List<Delegate> getDelegateList() {
        return delegateList;
    }
}
