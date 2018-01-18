package com.ftd.services.listener.search.bl.autofill;

import com.ftd.services.listener.search.config.AppConfigProperties;
import com.ftd.services.search.api.request.RequestType;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
@RefreshScope
@ConditionalOnProperty(name = "service.autofill.enabled")
public class AutofillFileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutofillFileLoader.class);

    @Value("${service.autofill.querySize}")
    private int autofillQuerySize;
    private AppConfigProperties appConfigProperties;
    private Map<String, List<String>> autofillSiteToKeywordsMap;
    private Map<String, Map<String, Set<String>>> autofillGlobalMap;
    private SolrDocumentUtil solrDocumentUtil;
    private EnhancedSolrClient enhancedSolrClient;


    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }

    @Autowired
    public void setEnhancedSolrClient(EnhancedSolrClient enhancedSolrClient) {
        this.enhancedSolrClient = enhancedSolrClient;
    }

    public Map<String, Map<String, Set<String>>> getAutofillGlobalMap() {
        return autofillGlobalMap;
    }

    public AutofillFileLoader() {
        autofillSiteToKeywordsMap = new HashMap<>();
        autofillGlobalMap = new HashMap<>();
    }

    @Autowired
    public void setAppConfigProperties(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }





    public Map<String, List<String>> getAutofillSiteToKeywordsMap() {
        return autofillSiteToKeywordsMap;
    }

    @PostConstruct
    @Scheduled(fixedRateString = "${service.autofill.keywordReloadRate}")
    public void loadAllAutofillFiles() {
        Map<String, String> autofillKeywordMap = appConfigProperties.getSitesAutofillKeywordMap();
        autofillKeywordMap.keySet()
                .forEach(siteId -> loadSingleAutofillFile(siteId, autofillKeywordMap.get(siteId)));

        LOGGER.info("Autofill keyword files loaded ...");

    }


    public void loadSingleAutofillFile(String siteId,
                                       String fileName) {
        List<String> keywordList = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream
                    .map(this::normalize)
                    .forEach(keywordList::add);
        } catch (IOException e) {
            LOGGER.error("Could not load file", e);
        }

        autofillSiteToKeywordsMap.put(siteId, keywordList);
        autofillGlobalMap.computeIfAbsent(siteId, k -> new HashMap<>());
        keywordList.forEach(e -> searchAndBuildKeyword(siteId, e));
    }

    private String normalize(String input) {
        return StringUtils.lowerCase(
                StringUtils.normalizeSpace(input.replaceAll("\\W", " ")));
    }

    public void searchAndBuildKeyword(String siteId, String keyword) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(keyword)
                .setRows(autofillQuerySize)
                .setFields(GlobalConstants.ID, GlobalConstants.PRIMARY_IMAGE)
//                .setFilterQueries("siteId:" + siteId)
                .setRequestHandler(GlobalConstants.FORWARD_SLASH + RequestType.SEARCH.getName());
        QueryResponse queryResponse = null;
        try {
            queryResponse = enhancedSolrClient.run(solrQuery);
        } catch (Exception e) {
            LOGGER.error("cannot execute solr query ", e);
        }
        if (queryResponse == null
                || queryResponse.getResults() == null
                || queryResponse.getResults().getNumFound() == 0) {
            return;
        }
        Map<String, Set<String>> pidMap = autofillGlobalMap.get(siteId);

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.ID,
                GlobalConstants.AUTOFILL + keyword.hashCode());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.PID,
                GlobalConstants.AUTOFILL + keyword.hashCode());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SITE_ID, siteId);
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TYPE, GlobalConstants.AUTOFILL);
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.AUTOFILL_KEYWORD, keyword);
        SolrDocumentList solrDocuments = queryResponse.getResults();
        short rows = 0;
        for (SolrDocument solrDocument : solrDocuments) {
            String pid = solrDocumentUtil.getFieldValue(solrDocument, GlobalConstants.ID);
            if (StringUtils.isEmpty(pid)) {
                continue;
            }
            pidMap.computeIfAbsent(pid, k -> new HashSet<>()).add(keyword);
            solrDocumentUtil.addField(
                    solrInputDocument,
                    GlobalConstants.IMAGE + (++rows) + "_s",
                    solrDocumentUtil.getFieldValue(solrDocument, GlobalConstants.PRIMARY_IMAGE));
        }
        enhancedSolrClient.updateDocs(Arrays.asList(solrInputDocument));

    }

}
