package com.capstoneproject.server.payload.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 5/24/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String password;
    private String studentId;
    private String fullName;
    private String email;
    private String avatar;
    private String phoneNumber;
    private Long classId;
    private Long roleId;
}
