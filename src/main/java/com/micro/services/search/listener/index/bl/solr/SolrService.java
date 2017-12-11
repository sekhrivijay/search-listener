package com.micro.services.search.listener.index.bl.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.util.concurrent.Future;

public interface SolrService {
    void updateJson(String jsonData);

    void updateDocs(SupplierWithException<UpdateResponse> solrProcess);

    Future<QueryResponse> run(SolrQuery solrQuery) throws Exception;

    int ping() throws Exception;
}
