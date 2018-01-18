package com.ftd.services.listener.search.bl.processor;

import com.ftd.services.listener.search.bl.bso.BsoEntity;
import com.ftd.services.listener.search.bl.bso.BsoFileLoader;
import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.config.AppConfigProperties;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.Map;

@Named("bsoDelegate")
public class BsoDelegate implements Delegate {
    private BsoFileLoader bsoFileLoader;
    private AppConfigProperties appConfigProperties;
    private SolrDocumentUtil solrDocumentUtil;

    @Autowired
    public void setAppConfigProperties(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
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
        Map<String, String> bsoFileMap = appConfigProperties.getSitesBsoMap();
        addBsoForASite(context, solrInputDocument);
//        bsoFileMap.keySet()
//                .forEach(siteId -> addBsoForASite(context, siteId, solrInputDocument));
        return solrInputDocument;
    }

    private void addBsoForASite(Context context, SolrInputDocument solrInputDocument) {
        String siteId = context.getSiteId();
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

        solrDocumentUtil.addField(solrInputDocument, "bsoOrderCnt_l", bsoEntity.getOrderCount());
        solrDocumentUtil.addField(solrInputDocument, "_bsoOrderAmt_d", bsoEntity.getOrderAmount());
        solrDocumentUtil.addField(solrInputDocument, "_bsoOrderMargin_d", bsoEntity.getMargin());

    }
}
