package com.capstoneproject.server.controller;

import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.payload.request.NewUserRequest;
import com.capstoneproject.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("accounts")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserEntity> addNewUser(@RequestBody NewUserRequest request){
        return ResponseEntity.ok(userService.addUser(request.getUsername(), request.getPassword(),
                request.getRoleId(), request.getFullName(), request.getEmail()));
    }
}
