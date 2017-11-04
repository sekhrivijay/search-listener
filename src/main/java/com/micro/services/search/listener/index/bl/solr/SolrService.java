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

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.function.Supplier;

@Named
public class SolrService {
    public static final String SPLIT = "split";
    public static final String CHILD_DOCUMENTS = "_childDocuments_";
    public static final String UPDATE_JSON_DOCS = "/update/json/docs";
    private static final int MAX_RETRY_TIMES = 10;
    private static final int WAIT_TIME = 500;

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

    private UpdateResponse getUpdateResponse(String jsonData) throws org.apache.solr.client.solrj.SolrServerException, java.io.IOException {
        ContentStreamUpdateRequest request = getContentStreamUpdateRequest();
        request.addContentStream(new ContentStreamBase.StringStream(jsonData));
        return request.process(solrClient);
    }

    @Timed
    @LogExecutionTime
    public void updateDocs(SupplierWithException<UpdateResponse> solrProcess) {
        for (short retryCount = 0; retryCount != MAX_RETRY_TIMES; ++retryCount) {
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
        if (retryCount < MAX_RETRY_TIMES) {
            try {
                LOGGER.info("Going to retry after " + WAIT_TIME * (retryCount + 1) + " milli seconds");
                Thread.sleep(WAIT_TIME * (retryCount + 1));
            } catch (InterruptedException e) {
                LOGGER.info("Exception occurred in waiting to retry to push to solr");
            }
        } else {
            LOGGER.error("GIVING UP . Cannot add this list of documents after trying " + MAX_RETRY_TIMES + " times");
        }
    }


    interface SupplierWithException<T> {
        T get() throws Exception ;
    }
}
