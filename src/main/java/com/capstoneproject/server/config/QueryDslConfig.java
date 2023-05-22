package com.capstoneproject.server.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Configuration
public class QueryDslConfig {
    @Bean
    public JPAQueryFactory jpaQueryBuilder(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
