package com.micro.services.search.listener.index.solr;


import com.codahale.metrics.annotation.Timed;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.util.ContentStreamBase;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SolrService {
    public static final String SPLIT = "split";
    public static final String CHILD_DOCUMENTS = "_childDocuments_";
    public static final String UPDATE_JSON_DOCS = "/update/json/docs";
    private static final Logger LOGGER = Logger.getLogger(SolrService.class);
    private static final int MAX_RETRY_TIMES = 10;
    private static final int WAIT_TIME = 500;

    @Inject
    private SolrClient solrClient;

    @Timed
    public void updateJson(String jsonData) {
        for (int retryCount = 0; retryCount != MAX_RETRY_TIMES; ++retryCount) {
            try {
                ContentStreamUpdateRequest request = getContentStreamUpdateRequest();
                request.addContentStream(new ContentStreamBase.StringStream(jsonData));
                UpdateResponse response = request.process(solrClient);
                if (response.getStatus() == 0) {
                    LOGGER.info("posted doc to solr successfully ");
                    break;
                } else {
                    LOGGER.error("Could not post doc to solr. Retrying ...");
                    waitForRetry(retryCount);
                }
            } catch (Exception e) {
                LOGGER.error("Exception in " + Thread.currentThread().getName() + ". Unable to post to Solr.", e);
                waitForRetry(retryCount);
            }
        }
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
}
