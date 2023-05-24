package com.capstoneproject.server.converter;

import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.payload.response.UserDTO;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dai.le-anh
 * @since 5/23/2023
 */

public class UserConverter {
    public static UserDTO map(UserEntity entity){
        return UserDTO.builder()
                .userId(entity.getUserId())
                .classId(entity.getClazz() != null ? entity.getClazz().getClassId() : -1)
                .avatar(StringUtils.defaultString(entity.getAvatar()))
                .className(entity.getClazz() != null ? entity.getClazz().getClassName() : "")
                .email(entity.getEmail())
                .avatar(entity.getAvatar())
                .score(entity.getScore())
                .phoneNumber(StringUtils.defaultString(entity.getPhoneNumber()))
                .fullName(entity.getFullName())
                .studentId(StringUtils.defaultString(entity.getStudentId()))
                .username(entity.getUsername())
                .roleId(entity.getRole().getRoleId())
                .role(entity.getRole().getRoleName())
                .build();
    }
}
