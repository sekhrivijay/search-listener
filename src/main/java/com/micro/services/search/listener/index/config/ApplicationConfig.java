package com.micro.services.search.listener.index.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
