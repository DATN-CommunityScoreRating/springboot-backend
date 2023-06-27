package com.capstoneproject.server.kafka.service;

import com.capstoneproject.server.kafka.message.SendActivityClearProofMessage;
import com.capstoneproject.server.kafka.message.SendClearProofMessage;

/**
 * @author dai.le-anh
 * @since 6/27/2023
 */

public interface ClearProofKafkaService {
    void sendActivityClearProof(SendActivityClearProofMessage message);

    void sendClearProof(SendClearProofMessage message);
}
