package com.capstoneproject.server.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ClearProofStatus {
    SEND_PROOF(1L, "SEND_PROOF"),
    CONFIRMED(2L, "CONFIRMED"),
    NOT_ACCEPTED(3L, "NOT_ACCEPTED");

    public final Long userActivityId;
    public final String status;
}
