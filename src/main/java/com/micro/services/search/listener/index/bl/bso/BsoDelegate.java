package com.micro.services.search.listener.index.bl.bso;

import com.micro.services.search.listener.index.bl.dm.Context;
import com.micro.services.search.listener.index.bl.processor.Delegate;
import com.micro.services.search.listener.index.bl.solr.SolrDocumentUtil;
import com.micro.services.search.listener.index.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.Map;

@Named("bsoDelegate")
public class BsoDelegate implements Delegate {
    private BsoFileLoader bsoFileLoader;
    private AppConfig appConfig;
    private SolrDocumentUtil solrDocumentUtil;

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }

    @Autowired
    public void setBsoFileLoader(BsoFileLoader bsoFileLoader) {
        this.bsoFileLoader = bsoFileLoader;
    }

    public SolrInputDocument process(Context context, SolrInputDocument solrInputDocument) {
        Map<String, String> bsoFileMap = appConfig.getSitesBsoMap();
        bsoFileMap.keySet()
                .forEach(siteId -> addBsoForASite(context, siteId, solrInputDocument));
        return solrInputDocument;
    }

    private void addBsoForASite(Context context, String siteId, SolrInputDocument solrInputDocument) {
        if (StringUtils.isEmpty(siteId) || StringUtils.isEmpty(context.getPid())) {
            return;
        }
        Map<String, BsoEntity> bsoEntityMap = bsoFileLoader.getBsoGlobalMap().get(siteId);
        if (bsoEntityMap == null) {
            return;
        }
        BsoEntity bsoEntity = bsoEntityMap.get(context.getPid());
        if (bsoEntity == null) {
            return;
        }

        solrDocumentUtil.addField(solrInputDocument, siteId + "_bso_order_cnt_l", bsoEntity.getOrderCount());
        solrDocumentUtil.addField(solrInputDocument, siteId + "_bso_order_amt_d", bsoEntity.getOrderAmount());
        solrDocumentUtil.addField(solrInputDocument, siteId + "_bso_order_margin_d", bsoEntity.getMargin());

    }
}
