package com.capstoneproject.server.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dai.le-anh
 * @since 6/3/2023
 */

@AllArgsConstructor
@Getter
public enum ActivityStatus {
    ACTIVE,
    EXPIRED,
    PENDING,
    FULLY,
    DISABLE
}
