package com.micro.services.search.listener.index.bl.solr;


import com.codahale.metrics.annotation.Timed;
import com.services.micro.commons.logging.annotation.LogExecutionTime;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.ContentStreamBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import java.util.List;

@Named
public class SolrService {
    public static final String SPLIT = "split";
    public static final String CHILD_DOCUMENTS = "_childDocuments_";
    public static final String UPDATE_JSON_DOCS = "/update/json/docs";

    @Value("${service.solrMaxRetryAttempts}")
    private int solrMaxRetryAttempts;

    @Value("${service.solrRetryInterval}")
    private int solrRetryInterval;

    private static final Logger LOGGER = Logger.getLogger(SolrService.class);

    private SolrClient solrClient;


    @Autowired
    public void setSolrClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    public void updateDocs(List<SolrInputDocument> solrInputDocumentList) {
        if (solrInputDocumentList == null || solrInputDocumentList.size() == 0) {
            return;
        }
        updateDocs(() -> solrClient.add(solrInputDocumentList));
    }

    public void updateJson(String jsonData) {
        updateDocs(() -> getUpdateResponse(jsonData));
    }

    private UpdateResponse getUpdateResponse(String jsonData) throws Exception {
        ContentStreamUpdateRequest request = getContentStreamUpdateRequest();
        request.addContentStream(new ContentStreamBase.StringStream(jsonData));
        return request.process(solrClient);
    }

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
        }
    }


    interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
