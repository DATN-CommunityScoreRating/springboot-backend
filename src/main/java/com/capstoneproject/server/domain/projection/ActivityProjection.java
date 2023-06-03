package com.capstoneproject.server.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityProjection {
    private Long activityId;
    private String activityName;
    private Timestamp startDate;
    private Timestamp endDate;
    private String location;
    private Integer maxQuantity;
    private Long totalParticipant;
    private Integer score;
    private Long createUserId;
    private Timestamp startRegister;
    private Timestamp endRegister;
}
