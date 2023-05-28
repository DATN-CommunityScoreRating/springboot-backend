package com.capstoneproject.server.config;

import com.capstoneproject.server.cache.JacksonRedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public JacksonRedisUtil jacksonRedisUtil(RedisConnectionFactory factory, @Qualifier("customObjectMapper") ObjectMapper objectMapper) {
        return new JacksonRedisUtil(factory, objectMapper);
    }
}
