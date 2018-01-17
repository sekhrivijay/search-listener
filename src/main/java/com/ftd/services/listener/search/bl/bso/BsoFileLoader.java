package com.ftd.services.listener.search.bl.bso;

import com.ftd.services.listener.search.config.AppConfigProperties;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@RefreshScope
@ConditionalOnProperty(name = "service.bso.enabled")
public class BsoFileLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BsoFileLoader.class);
    private AppConfigProperties appConfigProperties;
    private Map<String, Map<String, BsoEntity>> bsoGlobalMap;
    private static final int MIN_TOKEN_LENGTH = 5;
    private static final int ORDER_CNT_INDEX = 2;
    private static final int ORDER_AMT_INDEX = 3;
    private static final int MARGIN_INDEX = 4;

    public BsoFileLoader() {
        bsoGlobalMap = new HashMap<>();
    }


    @Autowired
    public void setAppConfigProperties(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    public Map<String, Map<String, BsoEntity>> getBsoGlobalMap() {
        return bsoGlobalMap;
    }

    @PostConstruct
    @Scheduled(fixedRateString = "${service.bso.fileReloadRate}")
    public void loadAllBsoFiles() {
        Map<String, String> bsoFileMap = appConfigProperties.getSitesBsoMap();
        bsoFileMap.keySet()
                .forEach(siteId -> loadSingleBsoFile(siteId, bsoFileMap.get(siteId)));
        LOGGER.info("Bso files loaded ...");

    }

    public void loadSingleBsoFile(String siteId,
                                  String fileName) {
        Map<String, BsoEntity> bsoEntityMap = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(e -> loadIntoMap(bsoEntityMap, e));
        } catch (IOException e) {
            LOGGER.error("Could not load file", e);
        }

        bsoGlobalMap.put(siteId, bsoEntityMap);
    }

    public void loadIntoMap(Map<String, BsoEntity> bsoEntityMap, String line) {
        if (StringUtils.isEmpty(line)) {
            return;
        }
        String tmpLine = StringUtils.normalizeSpace(line
                .replaceAll("\\" + GlobalConstants.DOLLAR, StringUtils.EMPTY)
                .replaceAll(GlobalConstants.COMMA, StringUtils.EMPTY));

        String[] tokens = tmpLine.split(GlobalConstants.SPACE);
        if (tokens.length < MIN_TOKEN_LENGTH) {
            LOGGER.info("Ignoring the line as length is less than 4 " + line);
            return;
        }


        long orderCount = NumberUtils.toLong(tokens[ORDER_CNT_INDEX], Long.MAX_VALUE);
        double orderAmount = NumberUtils.toDouble(tokens[ORDER_AMT_INDEX], Double.MAX_VALUE);
        double margin = NumberUtils.toDouble(tokens[MARGIN_INDEX], Double.MAX_VALUE);

        if (orderCount == Long.MAX_VALUE
                || orderAmount == Double.MAX_VALUE
                || margin == Double.MAX_VALUE
                ) {
            LOGGER.info("Ignoring the line as entries are not a number  " + line);
            return;
        }

        BsoEntity bsoEntity = new BsoEntity(orderCount, orderAmount, margin);
        bsoEntityMap.put(tokens[1], bsoEntity);
    }
}
