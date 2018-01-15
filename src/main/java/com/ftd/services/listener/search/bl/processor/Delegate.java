package com.ftd.services.listener.search.bl.processor;

import com.ftd.services.listener.search.bl.dm.Context;
import org.apache.solr.common.SolrInputDocument;

public interface Delegate {
    SolrInputDocument process(Context context, SolrInputDocument solrInputDocument);
}
