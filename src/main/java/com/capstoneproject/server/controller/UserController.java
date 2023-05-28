package com.capstoneproject.server.controller;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.payload.request.user.GetUserRequest;
import com.capstoneproject.server.payload.request.user.NewUserRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    public Response<OnlyIDDTO> addNewUser(@RequestBody NewUserRequest request){
        return userService.addUser(request);
    }

    @GetMapping
    public Response<PageDTO<UserDTO>> getAllUser(@ModelAttribute("request")GetUserRequest request){
        return userService.getListUser(request);
    }

    @GetMapping("{id}")
    public Response<UserDTO> getUserById(@PathVariable(name = "id") Long id){
        return userService.getUserById(id);
    }

    @PutMapping("{id}")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    public Response<OnlyIDDTO> updateUser(@PathVariable("id") Long id,@RequestBody NewUserRequest request){
        return userService.updateUser(id, request);
    }

    @DeleteMapping("{id}")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    public Response<NoContentDTO> deleteUser(@PathVariable("id") Long userId){
        return userService.deleteUser(userId);
    }


}
