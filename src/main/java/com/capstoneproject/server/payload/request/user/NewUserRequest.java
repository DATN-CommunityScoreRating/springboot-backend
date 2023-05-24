package com.capstoneproject.server.payload.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Getter
@Setter
@NoArgsConstructor
public class NewUserRequest {
    private String username;
    private String password;
    private Long roleId;
    private String fullName;
    private String email;
    private String avatar;
    private String phoneNumber;
    private Long classId;
    private String studentId;
}
