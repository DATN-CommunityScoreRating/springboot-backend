package com.capstoneproject.server.payload.response;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ClassDTO {
    private Long classId;
    private Long facultyId;
    private Long courseId;
    private String courseName;
    private String facultyName;
    private String className;
}
