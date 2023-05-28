package com.capstoneproject.server.common;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import lombok.*;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
public class CommunityBKDNPrincipal {
    private String username;
    private Long userId;
    private String email;
    private String studentId;
    private String role;
    private Integer score;

    public Boolean isAdmin(){
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.ADMIN);
    }

    public Boolean isStudent(){
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.STUDENT);
    }
}
