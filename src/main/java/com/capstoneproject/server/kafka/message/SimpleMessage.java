package com.capstoneproject.server.kafka.message;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/9/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SimpleMessage {
    private String message;
}
