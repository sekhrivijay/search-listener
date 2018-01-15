package com.ftd.services.listener.search.bl.solr;

public interface SupplierWithException<T> {
        T get() throws Exception;
}
