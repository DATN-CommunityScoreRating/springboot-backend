package com.capstoneproject.server.controller;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.payload.request.ImportRequest;
import com.capstoneproject.server.payload.response.ImportDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.payload.response.UploadDTO;
import com.capstoneproject.server.payload.response.UploadStudentDTO;
import com.capstoneproject.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@RestController
@Tag(name = "Student APIs")
@RequiredArgsConstructor
@RequestMapping("students")
public class StudentController {
    private final UserService userService;

    @Operation(summary = "Upload student")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    @PostMapping("upload")
    @Parameter(in = ParameterIn.QUERY, name = "file", description = "File csv import student")
    public Response<UploadDTO<UploadStudentDTO>> uploadStudent(@RequestParam(name = "file")MultipartFile request){
        return userService.uploadStudent(request);
    }


    @Operation(summary = "Import student")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    @PostMapping("import")
    public Response<ImportDTO> importStudent(@RequestBody ImportRequest request){
        return userService.importStudent(request);
    }


}
