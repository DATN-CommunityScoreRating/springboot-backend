package com.capstoneproject.server.kafka.service;

import com.capstoneproject.server.common.constants.Constant;
import com.capstoneproject.server.kafka.message.SimpleMessage;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @author dai.le-anh
 * @since 6/9/2023
 */

@Service
@Log4j2
public class StudentActivityProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StudentActivityProducerService(@Qualifier("CommunityScoreKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, Object message){
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            }
        });

    }

    public void sendMessage(SimpleMessage message){
        publish(Constant.KAFKA.TOPIC, message);
    }
}
