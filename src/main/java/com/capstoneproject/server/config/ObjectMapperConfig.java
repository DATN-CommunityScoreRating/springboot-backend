package com.capstoneproject.server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@Configuration
public class ObjectMapperConfig {

    @Bean("customObjectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false)
                .registerModule(new JavaTimeModule());
    }

}