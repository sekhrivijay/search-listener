package com.micro.services.search.listener.index.bl.autofill;

import com.micro.services.search.listener.index.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@RefreshScope
public class AutofillFileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutofillFileLoader.class);

    private AppConfig appConfig;
    private Map<String, List<String>> autofillGlobalMap;

    public AutofillFileLoader() {
        autofillGlobalMap = new HashMap<>();
    }

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public Map<String, List<String>> getAutofillGlobalMap() {
        return autofillGlobalMap;
    }

    @PostConstruct
    @Scheduled(fixedRateString = "${service.autofillKeywordReloadRate}")
    public void loadAllBsoFiles() {
        Map<String, String> autofillKeywordMap = appConfig.getSitesAutofillKeywordMap();
        autofillKeywordMap.keySet()
                .forEach(siteId -> loadSingleAutofillFile(siteId, autofillKeywordMap.get(siteId)));

        LOGGER.info("Autofill keyword files loaded ...");

    }


    public void loadSingleAutofillFile(String siteId,
                                       String fileName) {
        List<String> keywordList = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(keywordList::add);
        } catch (IOException e) {
            LOGGER.error("Could not load file", e);
        }

        autofillGlobalMap.put(siteId, keywordList);
    }

}
