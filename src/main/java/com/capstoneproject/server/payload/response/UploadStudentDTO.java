package com.capstoneproject.server.payload.response;

import lombok.*;

import java.util.Map;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UploadStudentDTO {
    private String className;
    private String studentId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Map<String, String> errors;
}
