package com.ftd.services.listener.search.bl.solr.impl;


import com.codahale.metrics.annotation.Timed;
import com.ftd.services.listener.search.bl.solr.SolrService;
import com.ftd.services.listener.search.bl.solr.SolrUtil;
import com.ftd.services.listener.search.bl.solr.SupplierWithException;
import com.services.micro.commons.logging.annotation.LogExecutionTime;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.ContentStreamBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("solrService")
public class SolrServiceImpl implements SolrService {
    public static final String SPLIT = "split";
    public static final String CHILD_DOCUMENTS = "_childDocuments_";
    public static final String UPDATE_JSON_DOCS = "/update/json/docs";
    //    private static final String SOLR_REQUEST = "search.solr.request";
    public static final QueryResponse FALLBACK_QUERY_RESPONSE = SolrUtil.getFallback();

    @Value("${service.solrMaxRetryAttempts}")
    private int solrMaxRetryAttempts;

    @Value("${service.solrRetryInterval}")
    private int solrRetryInterval;

    @Value("${service.collectionDestination}")
    private String collectionDestination;

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrServiceImpl.class);

    private SolrClient solrClient;

    private SolrUtil solrUtil;

    private SolrPing ping = new SolrPing();

    @Inject
    public void setSolrUtil(SolrUtil solrUtil) {
        this.solrUtil = solrUtil;
    }

    public SolrServiceImpl() {
        ping.getParams().add("distrib", "true");
        ping.getParams().add("qt", "/search");
    }

    @Autowired
    public void setSolrClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    public UpdateResponse deleteById(String id) {
        LOGGER.info("Deleting ID " + id);
        try {
            return solrClient.deleteById(id);
        } catch (Exception e) {
            LOGGER.error("Cannot delete the ID");
            throw new RuntimeException("Cannot delete the ID");
        }
    }

    public void updateDocs(List<SolrInputDocument> solrInputDocumentList) {

        if (solrInputDocumentList == null || solrInputDocumentList.size() == 0) {
            return;
        }
        LOGGER.info("solrDocument to send to SOLR " + solrInputDocumentList.toString());
        updateDocs(() -> solrClient.add(solrInputDocumentList));
    }

    @Override
    public void updateJson(String jsonData) {
        updateDocs(() -> getUpdateResponse(jsonData));
    }

    private UpdateResponse getUpdateResponse(String jsonData) throws Exception {
        ContentStreamUpdateRequest request = getContentStreamUpdateRequest();
        request.addContentStream(new ContentStreamBase.StringStream(jsonData));
        return request.process(solrClient);
    }

    @Override
    @Timed
    @LogExecutionTime
    public void updateDocs(SupplierWithException<UpdateResponse> solrProcess) {

        for (short retryCount = 0; retryCount != solrMaxRetryAttempts; ++retryCount) {
            try {
                UpdateResponse response = solrProcess.get();
                if (validate(retryCount, response)) {
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("Exception in " + Thread.currentThread().getName() + ". Unable to post to Solr.", e);
                waitForRetry(retryCount);
            }
        }
    }

    private boolean validate(short retryCount, UpdateResponse response) {
        if (response.getStatus() == 0) {
            LOGGER.info("posted doc to solr successfully ");
            return true;
        } else {
            LOGGER.error("Could not post doc to solr. Retrying ...");
            waitForRetry(retryCount);
        }
        return false;
    }

    private ContentStreamUpdateRequest getContentStreamUpdateRequest() {
        ContentStreamUpdateRequest request = new ContentStreamUpdateRequest(UPDATE_JSON_DOCS);
        request.setParam(SPLIT, "/|/" + CHILD_DOCUMENTS);
        return request;
    }


    private void waitForRetry(int retryCount) {
        if (retryCount < solrMaxRetryAttempts) {
            try {
                LOGGER.info("Going to retry after " + solrRetryInterval * (retryCount + 1) + " milli seconds");
                Thread.sleep(solrRetryInterval * (retryCount + 1));
            } catch (InterruptedException e) {
                LOGGER.info("Exception occurred in waiting to retry to push to solr");
            }
        } else {
            LOGGER.error("GIVING UP . Cannot add this list of documents after trying " +
                    solrMaxRetryAttempts + " times");
            throw new RuntimeException("GIVING UP . Cannot add this list of documents after trying \" +\n" +
                    "                    solrMaxRetryAttempts + \" times");
        }
    }

    public QueryResponse run(SolrQuery solrQuery) throws Exception {
        LOGGER.info("Solr Query is " + solrQuery.toQueryString());
        return solrUtil.runSolrCommand(solrClient, solrQuery);
    }


    public QueryResponse getFallback(SolrQuery solrQuery) {
        return FALLBACK_QUERY_RESPONSE;
    }

    public int ping() throws Exception {
        SolrPingResponse solrPingResponse = ping.process(solrClient, collectionDestination);
        return solrPingResponse.getStatus();
    }

}
