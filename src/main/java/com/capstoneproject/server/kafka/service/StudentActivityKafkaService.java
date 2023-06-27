package com.capstoneproject.server.kafka.service;

import com.capstoneproject.server.kafka.message.CancelUserActivityMessage;
import com.capstoneproject.server.kafka.message.RegistrationActivityMessage;

/**
 * @author dai.le-anh
 * @since 6/26/2023
 */

public interface StudentActivityKafkaService {
    void studentRegistrationActivity(RegistrationActivityMessage registrationActivityMessage);
    void cancelRegistrationActivity(CancelUserActivityMessage cancelUserActivityMessage);
}
