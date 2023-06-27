package com.capstoneproject.server.kafka.message;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/27/2023
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendActivityClearProofMessage {
    private String name;
    private String description;
    private Long userActivityId;
}
