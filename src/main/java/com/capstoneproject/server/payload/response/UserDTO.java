package com.capstoneproject.server.payload.response;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 5/23/2023
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String role;
    private Long roleId;
    private String email;
    private String avatar;
    private String studentId;
    private Integer score;
    private String phoneNumber;
    private String className;
    private Long classId;
    private String faculty;

}
