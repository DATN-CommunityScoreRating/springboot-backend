package com.capstoneproject.server.kafka.service;

import com.capstoneproject.server.common.constants.Constant;
import com.capstoneproject.server.kafka.message.SimpleMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * @author dai.le-anh
 * @since 6/9/2023
 */

@Service
@KafkaListener(
        topics = {
                Constant.KAFKA.TOPIC
        },
        containerFactory = "CommunityScoreKafkaListenerContainerFactory"
)
@Log4j2
public class StudentActivityConsumerService {
    @KafkaHandler
    public void receiveMessage(@Payload SimpleMessage chatMessage){
        log.info("_________________________RECEIVE_________________________");
        log.info("message: {}", chatMessage.getMessage());
        log.info("_________________________END______________________________");

    }
}
