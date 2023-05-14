package com.capstoneproject.server.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lkadai0801
 * @since 12/05/2023
 */

@Getter
@Setter
@Builder(buildMethodName = "newBuilder", setterPrefix = "set")
public class LoginSuccessResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String name;
    private String role;
    private Long userId;
    private String studentId;
    private Long roleId;
    private Long classId;
}
