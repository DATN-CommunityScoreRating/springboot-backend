package com.capstoneproject.server.kafka.consumer;

import com.capstoneproject.server.common.constants.Constant;
import com.capstoneproject.server.kafka.message.SendActivityClearProofMessage;
import com.capstoneproject.server.kafka.message.SendClearProofMessage;
import com.capstoneproject.server.kafka.service.ClearProofKafkaService;
import com.capstoneproject.server.service.ClearProofService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * @author dai.le-anh
 * @since 6/27/2023
 */

@Service
@KafkaListener(
        topics = {
                Constant.KAFKA.SEND_CLEAR_PROOF_TOPIC
        },
        containerFactory = "CommunityScoreKafkaListenerContainerFactory"
)
@Log4j2
@RequiredArgsConstructor
public class ClearProofKafkaConsumer {
    private final ClearProofKafkaService clearProofKafkaService;


    @KafkaHandler
    public void sendActivityClearProof(@Payload SendActivityClearProofMessage message){
        try {
            clearProofKafkaService.sendActivityClearProof(message);
        } catch (Exception e) {
            log.error("Cannot send activity clear proof for user activity id {}", message.getUserActivityId());
        }
    }

    @KafkaHandler
    public void sendClearProof(@Payload SendClearProofMessage message){
        try {
            clearProofKafkaService.sendClearProof(message);
        } catch (Exception e) {
            log.error("Cannot send clear proof for userId {}", message.getUserId());
        }
    }
}
