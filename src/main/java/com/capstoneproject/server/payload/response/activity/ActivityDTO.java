package com.capstoneproject.server.payload.response.activity;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "set", builderMethodName = "newBuilder")
public class ActivityDTO {
    private Long activityId;
    private String name;
    private String organization;
    private String status;
    private String startDate;
    private String endDate;
    private String location;
    private Integer score;
    private Integer totalParticipant;
    private Integer maxQuantity;
    private String description;
}
