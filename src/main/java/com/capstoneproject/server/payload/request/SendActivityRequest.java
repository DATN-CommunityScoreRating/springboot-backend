package com.capstoneproject.server.payload.request;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SendActivityRequest {
    private Long activityId;
    private String name;
    private String description;
}
