package com.micro.services.search.listener.index.config;

import com.google.gson.Gson;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "service")
@EnableConfigurationProperties
@Configuration
public class AppConfig {

    private Map<String, String> sitesBsoMap;

    public AppConfig() {
        sitesBsoMap = new HashMap<>();
    }

    public Map<String, String> getSitesBsoMap() {
        return sitesBsoMap;
    }

    public void setSitesBsoMap(Map<String, String> sitesBsoMap) {
        this.sitesBsoMap = sitesBsoMap;
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }




}
