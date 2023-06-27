package com.capstoneproject.server.kafka.consumer;

import com.capstoneproject.server.common.constants.Constant;
import com.capstoneproject.server.kafka.message.CancelUserActivityMessage;
import com.capstoneproject.server.kafka.message.RegistrationActivityMessage;
import com.capstoneproject.server.kafka.message.SimpleMessage;
import com.capstoneproject.server.kafka.service.StudentActivityKafkaService;
import lombok.RequiredArgsConstructor;
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
                Constant.KAFKA.STUDENT_REGISTRATION_TOPIC
        },
        containerFactory = "CommunityScoreKafkaListenerContainerFactory"
)
@Log4j2
@RequiredArgsConstructor
public class StudentActivityConsumer {
    private final StudentActivityKafkaService studentActivityService;
    @KafkaHandler
    public void receiveMessage(@Payload SimpleMessage chatMessage){
        log.info("_________________________RECEIVE_________________________");
        log.info("message: {}", chatMessage.getMessage());
        log.info("_________________________END______________________________");

    }

    @KafkaHandler
    public void studentRegistrationActivity(@Payload RegistrationActivityMessage registrationActivityMessage){
        try {
            studentActivityService.studentRegistrationActivity(registrationActivityMessage);

        } catch (Exception e) {
            log.error("Cannot register activity for userId {}", registrationActivityMessage.getUserId());
        }
    }

    @KafkaHandler
    public void cancelRegistrationActivity(@Payload CancelUserActivityMessage cancelUserActivityMessage){
        try {
            studentActivityService.cancelRegistrationActivity(cancelUserActivityMessage);

        } catch (Exception e) {
            log.error("Cannot cancel activity for userActivityId {}", cancelUserActivityMessage.getUserActivityId());
        }
    }

    @KafkaHandler(isDefault = true)
    public void unrecognisedMessage(Object message) {
        try {
            log.warn("Unrecognized message found with type {} and content {}", message.getClass().getSimpleName(), message.toString());
        } catch (Exception e) {
            log.error("Error when write message to string. {}", e.getMessage());
            log.warn("Unrecognized malformed message found with type {}", message.getClass().getSimpleName());
        }
    }
}
