package com.capstoneproject.server.service;

import com.capstoneproject.server.domain.entity.UserEntity;
import org.springframework.stereotype.Service;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

public interface UserService {
    UserEntity addUser(String username, String password, Long roleId, String fullName, String email);
}
