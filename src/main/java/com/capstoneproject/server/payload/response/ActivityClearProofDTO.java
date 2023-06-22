package com.capstoneproject.server.payload.response;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/21/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "set", builderMethodName = "newBuilder")
public class ActivityClearProofDTO {
    private Long clearProofId;
    private Long activityId;
    private String activityName;
    private String studentId;
    private String studentFullName;
    private Integer score;
    private String description;
}
