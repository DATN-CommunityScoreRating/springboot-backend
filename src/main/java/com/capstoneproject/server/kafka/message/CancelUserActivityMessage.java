package com.capstoneproject.server.kafka.message;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/26/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CancelUserActivityMessage {
    private Long userActivityId;
}
