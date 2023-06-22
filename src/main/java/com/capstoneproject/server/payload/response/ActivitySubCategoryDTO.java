package com.capstoneproject.server.payload.response;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivitySubCategoryDTO {
    private Long activitySubCategoryId;
    private String name;
    private Long activityCategoryId;
    private Integer minScore;
    private Integer maxScore;
}
