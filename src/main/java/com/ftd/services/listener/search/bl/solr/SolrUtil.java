package com.ftd.services.listener.search.bl.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Named
public class SolrUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrUtil.class);

    //    @LogExecutionTime
    public QueryResponse runSolrCommand(SolrClient solrClient, SolrQuery solrQuery) throws RuntimeException {
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrClient.query(solrQuery, SolrRequest.METHOD.POST);
        } catch (SolrServerException | IOException solrServerException) {
            LOGGER.error("Could not execute solr query " + solrQuery, solrServerException);
            throw new RuntimeException(solrServerException);
        }
        return queryResponse;
    }

    public static QueryResponse getFallback() {
        LOGGER.error("Going to fallback. Empty query response ");
        return new QueryResponse();
    }

    public QueryResponse getQueryResponse(Map<String,
            Future<QueryResponse>> futureMap,
                                          String key,
                                          long timeout)
            throws InterruptedException, ExecutionException, TimeoutException {
        QueryResponse queryResponse;
        try {
            queryResponse = futureMap.get(key).get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException timeoutException) {
            futureMap.get(key).cancel(true);
            throw timeoutException;
        }
        return queryResponse;
    }
}