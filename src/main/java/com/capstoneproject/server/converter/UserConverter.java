package com.capstoneproject.server.converter;

import com.capstoneproject.server.domain.entity.FacultyEntity;
import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.domain.prefetch.PrefetchEntityProvider;
import com.capstoneproject.server.payload.response.UserDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dai.le-anh
 * @since 5/23/2023
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConverter {

    private static PrefetchEntityProvider prefetchEntityProvider;
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
                .faculty(StringUtils.defaultString(prefetchEntityProvider.getFacultyEntityMap().
                        getOrDefault(entity.getClazz() != null ?
                                entity.getClazz().getClassId() : -1L,
                                new FacultyEntity()).getFacultyName()))
                .build();
    }

    public static void setPrefetchEntityProvider(PrefetchEntityProvider provider){
        prefetchEntityProvider = provider;
    }
}
