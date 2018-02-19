package com.ftd.services.listener.search.bl.autofill;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.util.BuilderUtil;
import com.ftd.services.listener.search.bl.util.ProductUtil;
import com.ftd.services.product.api.domain.response.Product;
import com.ftd.services.product.api.domain.response.ProductServiceResponse;
import com.ftd.services.search.api.request.RequestType;
import com.ftd.services.search.bl.clients.product.ProductClient;
import com.ftd.services.search.bl.clients.solr.EnhancedSolrClient;
import com.ftd.services.search.bl.clients.solr.util.SolrDocumentUtil;
import com.ftd.services.search.api.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Configuration
@RefreshScope
@ConditionalOnProperty(name = "service.autofill.enabled")
public class AutofillFileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutofillFileLoader.class);
    public static final List<String> ATTRIBUTE_NAMES = Arrays.asList(
            GlobalConstants.COLOR + GlobalConstants.UNDERSCORE_A,
            GlobalConstants.OCCASION + GlobalConstants.UNDERSCORE_A);

    @Value("${service.autofill.querySize}")
    private int autofillQuerySize;
    //    private AppConfigProperties appConfigProperties;
    private Map<String, List<String>> autofillSiteToKeywordsMap;
    private Map<String, Map<String, Set<String>>> autofillGlobalMap;
    private SolrDocumentUtil solrDocumentUtil;
    private EnhancedSolrClient enhancedSolrClient;
    private BuilderUtil builderUtil;
    private ProductClient productClient;

    private ProductUtil productUtil;

    @Autowired
    public void setProductUtil(ProductUtil productUtil) {
        this.productUtil = productUtil;
    }

    @Autowired
    public void setBuilderUtil(BuilderUtil builderUtil) {
        this.builderUtil = builderUtil;
    }

    @Autowired
    public void setProductClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Value("${service.autofill.keywordsFile.proflowers}")
    private Resource gcsResourceProflowers;

    @Value("${service.autofill.keywordsFile.ftd}")
    private Resource gcsResourceFtd;


    @Autowired
    public void setSolrDocumentUtil(SolrDocumentUtil solrDocumentUtil) {
        this.solrDocumentUtil = solrDocumentUtil;
    }

    @Autowired
    public void setEnhancedSolrClient(EnhancedSolrClient enhancedSolrClient) {
        this.enhancedSolrClient = enhancedSolrClient;
    }


    public Map<String, Map<String, Set<String>>> getAutofillGlobalMap() {
        return autofillGlobalMap;
    }

    public AutofillFileLoader() {
        autofillSiteToKeywordsMap = new HashMap<>();
        autofillGlobalMap = new HashMap<>();
    }


    public Map<String, List<String>> getAutofillSiteToKeywordsMap() {
        return autofillSiteToKeywordsMap;
    }

    @PostConstruct
    @Scheduled(fixedRateString = "${service.autofill.keywordReloadRate}")
    public void loadAllAutofillFiles() {
        loadSingleAutofillFile(GlobalConstants.PROFLOWERS, gcsResourceProflowers);
        loadSingleAutofillFile(GlobalConstants.FTD, gcsResourceFtd);
        LOGGER.info("Autofill keyword files loaded ...");

    }


    //    public void loadSingleAutofillFile(String siteId, String fileName) {
    public void loadSingleAutofillFile(String siteId, Resource gcsResource) {
        List<String> keywordList = new ArrayList<>();
        try {
            String fileContents = StreamUtils.copyToString(
                    gcsResource.getInputStream(),
                    Charset.defaultCharset()) + StringUtils.LF;
            Arrays.stream(StringUtils.splitPreserveAllTokens(fileContents, StringUtils.LF))
                    .map(this::normalize)
                    .forEach(keywordList::add);
        } catch (IOException e) {
            LOGGER.error("Could not load file", e);
        }


        autofillSiteToKeywordsMap.put(siteId, keywordList);
        autofillGlobalMap.computeIfAbsent(siteId, k -> new HashMap<>());
        keywordList.forEach(e -> searchAndBuildKeyword(siteId, e));
    }

    private String normalize(String input) {
        return StringUtils.lowerCase(
                StringUtils.normalizeSpace(input.replaceAll("\\W", " ")));
    }

    public void searchAndBuildKeyword(String siteId, String keyword) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(keyword)
                .setRows(autofillQuerySize)
//                .setFields(GlobalConstants.PID, GlobalConstants.PRIMARY_IMAGE)
                .setFields(GlobalConstants.PID)
                .addFilterQuery(GlobalConstants.SITE_ID + GlobalConstants.COLON + siteId)
                .addFilterQuery(GlobalConstants.TYPE + GlobalConstants.COLON + GlobalConstants.DEFAULT)
                .setRequestHandler(GlobalConstants.FORWARD_SLASH + RequestType.SEARCH.getName());
        QueryResponse queryResponse = null;
        try {
            queryResponse = enhancedSolrClient.run(solrQuery);
        } catch (Exception e) {
            LOGGER.error("cannot execute solr query ", e);
        }
        if (queryResponse == null
                || queryResponse.getResults() == null
                || queryResponse.getResults().getNumFound() == 0) {
            return;
        }
        Map<String, Set<String>> pidMap = autofillGlobalMap.get(siteId);

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.ID,
                GlobalConstants.AUTOFILL + siteId + keyword.hashCode());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.PID,
                GlobalConstants.AUTOFILL + keyword.hashCode());
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.SITE_ID, siteId);
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.TYPE, GlobalConstants.AUTOFILL);
        solrDocumentUtil.addField(solrInputDocument, GlobalConstants.AUTOFILL_KEYWORD, keyword);

        SolrDocumentList solrDocuments = queryResponse.getResults();
        short rows = 0;
        for (SolrDocument solrDocument : solrDocuments) {
            try {
                String pid = solrDocumentUtil.getFieldValue(solrDocument, GlobalConstants.PID);
                if (StringUtils.isEmpty(pid)) {
                    continue;
                }

                Context context = Context.ContextBuilder.aContext()
                        .withPid(pid)
                        .withSiteId(siteId)
                        .build();
                ProductServiceResponse productServiceResponse =
                        productClient.callProductService(
                                builderUtil.buildSearchServiceRequest(context),
                                builderUtil.builcSearchServiceResponse(context));


                productUtil.validateResponse(context, productServiceResponse);
                Optional<Product> productOptional = productServiceResponse.getProducts()
                        .stream()
                        .findFirst();
                if (productOptional.isPresent()) {
                    Product product = productOptional.get();
                    buildSolrDocument(context, solrInputDocument, product);

                    String primaryImage = productUtil.getPrimaryImage(product);
                    if (StringUtils.isNotEmpty(primaryImage)) {
                        solrDocumentUtil.addField(
                                solrInputDocument,
                                GlobalConstants.PID + (++rows) + "_s",
                                pid);
                        solrDocumentUtil.addField(
                                solrInputDocument,
                                GlobalConstants.IMAGE + (rows) + "_s",
                                primaryImage);
                    }

                    pidMap.computeIfAbsent(pid, k -> new HashSet<>()).add(keyword);

//                    solrDocumentUtil.addField(
//                            solrInputDocument,
//                            GlobalConstants.IMAGE + (++rows) + "_s",
//                            solrDocumentUtil.getFieldValue(solrDocument, GlobalConstants.PRIMARY_IMAGE));
                }

            } catch (Exception e) {
                LOGGER.error("Exception processing the keyword " + keyword, e);
            }
        }
        enhancedSolrClient.updateDocs(Arrays.asList(solrInputDocument));

    }

    private void buildSolrDocument(Context context, SolrInputDocument solrInputDocument, Product product) {
        productUtil.addCategories(context, solrInputDocument, product);
        productUtil.addAttributes(context,
                solrInputDocument,
                product,
                ATTRIBUTE_NAMES);
    }

}
