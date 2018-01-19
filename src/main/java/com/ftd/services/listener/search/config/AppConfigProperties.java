package com.ftd.services.listener.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "service")
@EnableConfigurationProperties
public class AppConfigProperties {
    //    private Map<String, String> sitesBsoMap;
//    private Map<String, String> sitesAutofillKeywordMap;
    private Map<String, Resource> sitesAutofillKeywordMap;

    public AppConfigProperties() {
//        sitesBsoMap = new HashMap<>();
        sitesAutofillKeywordMap = new HashMap<>();
    }

//    public Map<String, String> getSitesBsoMap() {
//        return sitesBsoMap;
//    }
//
//    public void setSitesBsoMap(Map<String, String> sitesBsoMap) {
//        this.sitesBsoMap = sitesBsoMap;
//    }

//    public Map<String, String> getSitesAutofillKeywordMap() {
//        return sitesAutofillKeywordMap;
//    }
//
//    public void setSitesAutofillKeywordMap(Map<String, String> sitesAutofillKeywordMap) {
//        this.sitesAutofillKeywordMap = sitesAutofillKeywordMap;
//    }

    public Map<String, Resource> getSitesAutofillKeywordMap() {
        return sitesAutofillKeywordMap;
    }

    public void setSitesAutofillKeywordMap(Map<String, Resource> sitesAutofillKeywordMap) {
        this.sitesAutofillKeywordMap = sitesAutofillKeywordMap;
    }
}
