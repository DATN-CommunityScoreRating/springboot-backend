package com.capstoneproject.server.payload.response;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/20/2023
 */

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentClearProofDTO {
    private Long userId;
    private String studentId;
    private String faculty;
    private String clearProofName;
    private Integer score;
    private String categoryName;
    private String startDate;
    private String endDate;
    private Long clearProofId;
    private String status;
    private String studentFullName;
    private String description;
    private Integer maxScore;
    private Integer minScore;
    private Integer clearProofScore;
}
