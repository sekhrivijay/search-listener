package com.ftd.services.listener.search.bl.util;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.search.api.request.SearchServiceRequest;
import com.ftd.services.search.api.response.Document;
import com.ftd.services.search.api.response.SearchServiceResponse;
import com.ftd.services.search.api.GlobalConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class BuilderUtil {
    public SearchServiceRequest buildSearchServiceRequest(Context context) {
        return SearchServiceRequest
                .SearchServiceRequestBuilder
                .aSearchServiceRequest()
                .withSiteId(context.getSiteId())
                .build();
    }

    public SearchServiceResponse buildSearchServiceResponse(Context context) {
        Document document = new Document();
        Map<String, Object> map = new HashMap<>();
        map.put(GlobalConstants.PID, context.getPid());
        document.setRecord(map);
        return SearchServiceResponse
                .SearchServiceResponseBuilder
                .aSearchServiceResponse()
                .withDocumentList(Arrays.asList(document))
                .build();
    }
}
