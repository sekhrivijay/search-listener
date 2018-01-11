package com.micro.services.search.listener.index.bl.autofill;

import com.micro.services.search.api.request.RequestType;
import com.micro.services.search.config.GlobalConstants;
import com.micro.services.search.listener.index.bl.solr.SolrDocumentUtil;
import com.micro.services.search.listener.index.bl.solr.SolrService;
import com.micro.services.search.listener.index.config.AppConfig;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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
public class AutofillFileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutofillFileLoader.class);
    public static final String SMALLIMAGE = "smallimage";

    @Value("${service.autofillQuerySize}")
    private int autofillQuerySize;
    private AppConfig appConfig;
    private Map<String, List<String>> autofillSiteToKeywordsMap;
    private Map<String, Map<String, Set<String>>> autofillGlobalMap;
    private SolrService solrService;
    private SolrDocumentUtil solrDocumentUtil;


    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }

    @Inject
    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
    }

    public Map<String, Map<String, Set<String>>> getAutofillGlobalMap() {
        return autofillGlobalMap;
    }

    public AutofillFileLoader() {
        autofillSiteToKeywordsMap = new HashMap<>();
        autofillGlobalMap = new HashMap<>();
    }

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public Map<String, List<String>> getAutofillSiteToKeywordsMap() {
        return autofillSiteToKeywordsMap;
    }

    @PostConstruct
    @Scheduled(fixedRateString = "${service.autofillKeywordReloadRate}")
    public void loadAllAutofillFiles() {
        Map<String, String> autofillKeywordMap = appConfig.getSitesAutofillKeywordMap();
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
        return StringUtils.normalizeSpace(input.replaceAll("\\W", " "));
    }

    public void searchAndBuildKeyword(String siteId, String keyword) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(keyword)
                .setRows(autofillQuerySize)
                .setFields(GlobalConstants.ID, SMALLIMAGE)
//                .setFilterQueries("siteId:" + siteId)
                .setRequestHandler(GlobalConstants.FORWARD_SLASH + RequestType.SEARCH.getName());
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrService.run(solrQuery);
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
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.ID, "af" + keyword.hashCode());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SITE_ID, siteId);
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TYPE, "autofill");
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.KEYWORD, keyword);
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
                    "image_" + (++rows) + "_s",
                    solrDocumentUtil.getFieldValue(solrDocument, SMALLIMAGE));
        }
        solrService.updateDocs(Arrays.asList(solrInputDocument));

    }

}
