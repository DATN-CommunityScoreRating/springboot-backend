package com.capstoneproject.server.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 6/26/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationActivityMessage {
    private Long userId;
    private Long activityId;
}
