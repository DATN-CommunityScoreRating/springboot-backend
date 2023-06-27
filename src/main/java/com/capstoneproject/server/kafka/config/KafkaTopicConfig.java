package com.capstoneproject.server.kafka.config;

import com.capstoneproject.server.common.constants.Constant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author dai.le-anh
 * @since 6/9/2023
 */

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic topic(){
        return TopicBuilder.name(Constant.KAFKA.STUDENT_REGISTRATION_TOPIC)
                .partitions(10)
                .replicas(3)
                .build();
    }
}
