package com.capstoneproject.server.payload.response;

import lombok.*;

import java.util.Map;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadClassDTO {
    private String course;
    private String faculty;
    private String className;
    private Map<String, String> error;
}
