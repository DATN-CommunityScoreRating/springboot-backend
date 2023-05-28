package com.capstoneproject.server.controller;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.payload.request.user.GetUserRequest;
import com.capstoneproject.server.payload.request.user.NewUserRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User APIs")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    @Operation(summary = "Add User")
    public Response<OnlyIDDTO> addNewUser(@RequestBody NewUserRequest request){
        return userService.addUser(request);
    }

    @GetMapping
    @Operation(summary = "List users")
    @Parameters({
            @Parameter(in = QUERY, name = "page", description = "Requested page. Start from 1. Default to 1 if not given", example = "1", schema = @Schema(minimum = "1")),
            @Parameter(in = QUERY, name = "size", description = "Requested page size. Default to 100 if not given", example = "100", schema = @Schema(minimum = "1")),
            @Parameter(in = QUERY, name = "sort", description = "Sort property. Omitted if not given", example = "lastName",
                    schema = @Schema(allowableValues = {"course", "faculty", "className"})),
            @Parameter(in = QUERY, name = "direction", description = "Sort direction. Accepted values - asc,desc. Omitted if sort not given. Default to asc",
                    example = "asc", schema = @Schema(allowableValues = {"asc", "desc"})),
            @Parameter(in = QUERY, name = "roleId", description = "Filter with role. Not apply if not given", example = "1"),
            @Parameter(in = QUERY, name = "classId", description = "Filter with class. Not apply if not given", example = "1"),
            @Parameter(in = QUERY, name = "searchTerm", description = "Search with keyword, not apply if not given", example = "Nhat"),
            @Parameter(in = QUERY, name = "Faculty", description = "Filter with class. Not apply if not given", example = "1")
    })
    public Response<PageDTO<UserDTO>> getAllUser(@ModelAttribute("request")GetUserRequest request){
        return userService.getListUser(request);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get user by Id")
    public Response<UserDTO> getUserById(@PathVariable(name = "id") Long id){
        return userService.getUserById(id);
    }

    @PutMapping("{id}")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    @Operation(summary = "Update user")
    public Response<OnlyIDDTO> updateUser(@PathVariable("id") Long id,@RequestBody NewUserRequest request){
        return userService.updateUser(id, request);
    }

    @DeleteMapping("{id}")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    @Operation(summary = "Delete user")
    public Response<NoContentDTO> deleteUser(@PathVariable("id") Long userId){
        return userService.deleteUser(userId);
    }


}
