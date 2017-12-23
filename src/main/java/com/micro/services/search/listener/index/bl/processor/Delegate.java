package com.micro.services.search.listener.index.bl.processor;

import com.micro.services.search.listener.index.bl.dm.Context;
import org.apache.solr.common.SolrInputDocument;

public interface Delegate {
    SolrInputDocument process(Context context, SolrInputDocument solrInputDocument);
}
