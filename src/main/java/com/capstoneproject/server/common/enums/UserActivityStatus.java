package com.capstoneproject.server.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author dai.le-anh
 * @since 6/4/2023
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserActivityStatus {
    REGISTERED(1L, "REGISTERED"),
    SEND_PROOF(2L, "SEND_PROOF"),
    CONFIRMED(3L, "CONFIRMED");

    public final Long userActivityId;
    public final String status;

}
