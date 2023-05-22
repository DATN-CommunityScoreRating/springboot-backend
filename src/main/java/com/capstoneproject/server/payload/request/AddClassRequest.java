package com.capstoneproject.server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class AddClassRequest {
    private String className;
    private Long courseId;
    private Long facultyId;
}
