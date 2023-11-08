package com.colphacy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class EnvironmentVariableConfig {
    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;

    @Bean
    public Integer getDefaultPageSize() {
        return defaultPageSize;
    }
}
