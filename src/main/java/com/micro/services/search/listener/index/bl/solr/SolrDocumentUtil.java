package com.micro.services.search.listener.index.bl.solr;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SolrDocumentUtil {
    public List normalize(List<String> input) {
        List<String> toReturn = new ArrayList<>();
        for (String value : input) {
            if (!StringUtils.isEmpty(value)) {
                toReturn.add(value);
            }
        }
        return toReturn;
    }


    public String getFieldValue(SolrDocument solrDocument, String field) {
        Object object = solrDocument.getFieldValue(field);
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

    public String getFieldValue(SolrInputDocument solrInputDocument, String field) {
        Object object = solrInputDocument.getFieldValue(field);
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

    public List<String> getFieldValues(SolrDocument solrDocument, String field) {
        if (solrDocument == null) {
            return null;
        }
        Object o = solrDocument.getFieldValues(field);
        if (o == null) {
            return null;
        }
        List<String> toReturn = new ArrayList<>();
        for (Object object: solrDocument.getFieldValues(field)) {
            if (object != null) {
                toReturn.add(String.valueOf(object));
            }
        }
        return toReturn;
    }
    public void setField(SolrInputDocument solrInputDocument, String key, Object value) {
        if (value == null || StringUtils.isEmpty(key)) {
            return;
        }
        solrInputDocument.setField(key, value);
    }

    public void addField(SolrInputDocument solrInputDocument, String key, Object value) {
        if (value == null || StringUtils.isEmpty(key)) {
            return;
        }
        solrInputDocument.addField(key, value);
    }


}
