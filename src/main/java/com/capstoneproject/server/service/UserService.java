package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.request.ImportRequest;
import com.capstoneproject.server.payload.request.user.GetUserRequest;
import com.capstoneproject.server.payload.request.user.NewUserRequest;
import com.capstoneproject.server.payload.request.user.UpdateUserRequest;
import com.capstoneproject.server.payload.response.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

public interface UserService {
    Response<OnlyIDDTO> addUser(NewUserRequest request);
    Response<PageDTO<UserDTO>> getListUser(GetUserRequest request);
    Response<UserDTO> getUserById(Long userId);
    Response<OnlyIDDTO> updateUser(Long userId, NewUserRequest request);
    Response<NoContentDTO> deleteUser(Long userId);

    Response<UploadDTO<UploadStudentDTO>> uploadStudent(MultipartFile request);

    Response<ImportDTO> importStudent(ImportRequest request);

    Response<UserDTO> getMyAccount();
}
