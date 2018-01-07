package com.micro.services.search.listener.index.bl.autofill;

import com.micro.services.search.listener.index.bl.bso.BsoEntity;
import com.micro.services.search.listener.index.bl.dm.Context;
import com.micro.services.search.listener.index.bl.processor.Delegate;
import com.micro.services.search.listener.index.bl.solr.SolrDocumentUtil;
import com.micro.services.search.listener.index.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named("autofillDelegate")
public class AutofillDelegate implements Delegate {
    private AutofillFileLoader autofillFileLoader;
    private AppConfig appConfig;
    private SolrDocumentUtil solrDocumentUtil;

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

        List<String> keywordList = autofillFileLoader.getAutofillGlobalMap().get(siteId);
        if (keywordList == null) {
            return;
        }



        String productName = solrDocumentUtil.getFieldValue(solrInputDocument,"name");
        String description = solrDocumentUtil.getFieldValue(solrInputDocument,"description");

        keywordList.forEach(e -> checkAndAddKeyword(solrInputDocument, e, productName, description));


    }
    private void checkAndAddKeyword(SolrInputDocument solrInputDocument, String keyword, String productName, String description) {
        if(isSimilar("", "")) {
            solrDocumentUtil.addField(solrInputDocument, "keyword", keyword);
        }
    }
    private boolean isSimilar(String term1, String term2) {
        return false;
    }
}
