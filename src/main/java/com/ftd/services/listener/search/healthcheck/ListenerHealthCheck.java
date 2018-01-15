package com.ftd.services.listener.search.healthcheck;

import com.ftd.services.listener.search.bl.solr.SolrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ListenerHealthCheck implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerHealthCheck.class);
    private SolrService solrService;

    public ListenerHealthCheck() {
    }

    @Autowired
    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
    }

    @Override
    public Health health() {
        try {
            int res = solrService.ping();
            if (res != 0) {
                return Health.down().build();
            }
        } catch (Exception e) {
            LOGGER.error("Health check failed ... ", e);
            return Health.down().build();
        }

        return Health.up().build();
    }
}
