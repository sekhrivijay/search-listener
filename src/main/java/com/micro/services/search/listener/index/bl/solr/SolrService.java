package com.micro.services.search.listener.index.bl.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.util.List;
import java.util.concurrent.Future;

public interface SolrService {
    void updateJson(String jsonData);

    void updateDocs(SupplierWithException<UpdateResponse> solrProcess);
    void updateDocs(List<SolrInputDocument> solrInputDocumentList);
    Future<QueryResponse> run(SolrQuery solrQuery) throws Exception;
    UpdateResponse deleteById(String id) throws Exception;

    int ping() throws Exception;
}
