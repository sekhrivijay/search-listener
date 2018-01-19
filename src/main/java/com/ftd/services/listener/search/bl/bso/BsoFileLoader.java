package com.ftd.services.listener.search.bl.bso;

import com.ftd.services.listener.search.config.AppConfigProperties;
import com.ftd.services.search.config.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@RefreshScope
@ConditionalOnProperty(name = "service.bso.enabled")
public class BsoFileLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BsoFileLoader.class);
    public static final String PFC = "PFC";
    private AppConfigProperties appConfigProperties;
    private Map<String, Map<String, BsoEntity>> bsoGlobalMap;
    private static final int MIN_TOKEN_LENGTH = 6;
    private static final int SITE_INDEX = 1;
    private static final int PID_INDEX = 2;
    private static final int ORDER_CNT_INDEX = 3;
    private static final int ORDER_AMT_INDEX = 4;
    private static final int MARGIN_INDEX = 5;

    @Value("${service.bso.file}")
    private String productFeedFile;


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
    public void loadFeedFile() {
        try (Stream<String> stream = Files.lines(Paths.get(productFeedFile))) {
            stream.forEach(this::loadIntoMap);
        } catch (IOException e) {
            LOGGER.error("Could not load file", e);
        }
        LOGGER.info("Bso files loaded ...");
    }

    public void loadIntoMap(String line) {
        List<String> tokens = Arrays.asList(StringUtils.splitPreserveAllTokens(line, GlobalConstants.COMMA));
        if (tokens.size() != MIN_TOKEN_LENGTH) {
            LOGGER.info("Ignoring the line as it does not have  " + MIN_TOKEN_LENGTH + " tokens " + line);
            return;
        }
        String siteIdFromLine = getNormalizedSite(tokens.get(SITE_INDEX));
        String pidFromLine = tokens.get(PID_INDEX);
        long orderCount = NumberUtils.toLong(tokens.get(ORDER_CNT_INDEX), Long.MAX_VALUE);
        double orderAmount = NumberUtils.toDouble(tokens.get(ORDER_AMT_INDEX), Double.MAX_VALUE);
        double margin = NumberUtils.toDouble(tokens.get(MARGIN_INDEX), Double.MAX_VALUE);


        if (orderCount == Long.MAX_VALUE
                || orderAmount == Double.MAX_VALUE
                || margin == Double.MAX_VALUE
                ) {
            LOGGER.info("Ignoring the line as entries are not a number  " + line);
            return;
        }
        bsoGlobalMap.computeIfAbsent(siteIdFromLine, k -> new HashMap<>());
        bsoGlobalMap.get(siteIdFromLine).put(pidFromLine, new BsoEntity(orderCount, orderAmount, margin));
    }


    private String getNormalizedSite(String siteId) {
        if (StringUtils.isEmpty(siteId)) {
            return StringUtils.EMPTY;
        }
        if (siteId.equalsIgnoreCase(GlobalConstants.FTD)) {
            return GlobalConstants.FTD;
        }
        if (siteId.equalsIgnoreCase(PFC)) {
            return GlobalConstants.PROFLOWERS;
        }
        return siteId;
    }
//    public void loadAllBsoFiles() {
//        Map<String, String> bsoFileMap = appConfigProperties.getSitesBsoMap();
//        bsoFileMap.keySet()
//                .forEach(siteId -> loadSingleBsoFile(siteId, bsoFileMap.get(siteId)));
//        LOGGER.info("Bso files loaded ...");
//
//    }
//
//    public void loadSingleBsoFile(String siteId,
//                                  String fileName) {
//        Map<String, BsoEntity> bsoEntityMap = new HashMap<>();
//        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
//            stream.forEach(e -> loadIntoMap(bsoEntityMap, e));
//        } catch (IOException e) {
//            LOGGER.error("Could not load file", e);
//        }
//
//        bsoGlobalMap.put(siteId, bsoEntityMap);
//    }
//
//    public void loadIntoMap(Map<String, BsoEntity> bsoEntityMap, String line) {
//        if (StringUtils.isEmpty(line)) {
//            return;
//        }
//        String tmpLine = StringUtils.normalizeSpace(line
//                .replaceAll("\\" + GlobalConstants.DOLLAR, StringUtils.EMPTY)
//                .replaceAll(GlobalConstants.COMMA, StringUtils.EMPTY));
//
//        String[] tokens = tmpLine.split(GlobalConstants.SPACE);
//        if (tokens.length < MIN_TOKEN_LENGTH) {
//            LOGGER.info("Ignoring the line as length is less than 4 " + line);
//            return;
//        }
//
//
//        long orderCount = NumberUtils.toLong(tokens[ORDER_CNT_INDEX], Long.MAX_VALUE);
//        double orderAmount = NumberUtils.toDouble(tokens[ORDER_AMT_INDEX], Double.MAX_VALUE);
//        double margin = NumberUtils.toDouble(tokens[MARGIN_INDEX], Double.MAX_VALUE);
//
//        if (orderCount == Long.MAX_VALUE
//                || orderAmount == Double.MAX_VALUE
//                || margin == Double.MAX_VALUE
//                ) {
//            LOGGER.info("Ignoring the line as entries are not a number  " + line);
//            return;
//        }
//
//        BsoEntity bsoEntity = new BsoEntity(orderCount, orderAmount, margin);
//        bsoEntityMap.put(tokens[1], bsoEntity);
//    }
}
