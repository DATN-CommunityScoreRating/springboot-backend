package com.capstoneproject.server.util;

import com.capstoneproject.server.common.CommunityBKDNPrincipal;
import com.capstoneproject.server.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;
    public CommunityBKDNPrincipal getPrincipal(){
        if (SecurityContextHolder.getContext().getAuthentication() != null){
            String username = (String) (SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
            var principal = userRepository.findByUsername(username).get();
            return CommunityBKDNPrincipal.newBuilder()
                    .setUsername(username)
                    .setRoleId(principal.getRole().getRoleId())
                    .setUserId(principal.getUserId())
                    .setScore(principal.getScore())
                    .setEmail(principal.getEmail())
                    .setRole(principal.getRole().getRoleName())
                    .setStudentId(StringUtils.defaultString(principal.getStudentId()))
                    .build();
        }
        return null;
    }
}