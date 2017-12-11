package com.micro.services.search.listener.index.bl.solr;

public interface SupplierWithException<T> {
        T get() throws Exception;
}
