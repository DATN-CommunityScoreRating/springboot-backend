package com.capstoneproject.server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FacultyDTO {
    private Long facultyId;
    private String facultyName;
}
