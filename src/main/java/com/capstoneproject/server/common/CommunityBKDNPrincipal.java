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
    private Long roleId;

    public Boolean isAdmin(){
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.ADMIN);
    }

    public Boolean isStudent(){
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.STUDENT);
    }

    public Boolean isFaculty() {
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.FACULTY);
    }

    public Boolean isUnion() {
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.UNION);
    }

    public Boolean isYouthUnion(){
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.YOUTH_UNION);
    }

    public Boolean isClass(){
        return role.equalsIgnoreCase(CommunityBKDNPermission.Role.CLASS);
    }
}
