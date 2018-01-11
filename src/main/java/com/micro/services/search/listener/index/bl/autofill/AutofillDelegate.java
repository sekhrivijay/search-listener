package com.micro.services.search.listener.index.bl.autofill;

import com.micro.services.search.config.GlobalConstants;
import com.micro.services.search.listener.index.bl.dm.Context;
import com.micro.services.search.listener.index.bl.processor.Delegate;
import com.micro.services.search.listener.index.bl.solr.SolrDocumentUtil;
import com.micro.services.search.listener.index.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;

@Named("autofillDelegate")
public class AutofillDelegate implements Delegate {
    private AutofillFileLoader autofillFileLoader;
    private AppConfig appConfig;
    private SolrDocumentUtil solrDocumentUtil;
//    private LevenshteinDistance levenshteinDistance;

//    @PostConstruct
//    public void setLevenshteinDistance() {
//        this.levenshteinDistance = LevenshteinDistance.getDefaultInstance();
//    }

    @Autowired
    public void setAutofillFileLoader(AutofillFileLoader autofillFileLoader) {
        this.autofillFileLoader = autofillFileLoader;
    }

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }

    @Override
    public SolrInputDocument process(Context context, SolrInputDocument solrInputDocument) {
        Map<String, String> autofillKeywordMap = appConfig.getSitesAutofillKeywordMap();
        autofillKeywordMap.keySet()
                .forEach(siteId -> addKeywordForASite(context, siteId, solrInputDocument));
        return solrInputDocument;
    }

    private void addKeywordForASite(Context context, String siteId, SolrInputDocument solrInputDocument) {
        if (StringUtils.isEmpty(siteId) || StringUtils.isEmpty(context.getPid())) {
            return;
        }

        Map<String, Set<String>> pidToKeywordListMap = autofillFileLoader.getAutofillGlobalMap().get(siteId);
        if (pidToKeywordListMap == null) {
            return;
        }
        Set<String> keywordList = pidToKeywordListMap.get(context.getPid());
        if (keywordList == null) {
            return;
        }
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.KEYWORD, keywordList);


    }


}
