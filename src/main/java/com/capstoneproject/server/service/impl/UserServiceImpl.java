package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.converter.UserConverter;
import com.capstoneproject.server.domain.entity.RoleEntity;
import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.domain.repository.ClassRepository;
import com.capstoneproject.server.domain.repository.RoleRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.domain.repository.dsl.UserDslRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.payload.request.user.GetUserRequest;
import com.capstoneproject.server.payload.request.user.NewUserRequest;
import com.capstoneproject.server.payload.request.user.UpdateUserRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.UserService;
import com.capstoneproject.server.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDslRepository userDslRepository;
    private final ClassRepository classRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() ->
                new ObjectNotFoundException("username", username));
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>(List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()))));
    }

    @Override
    public Response<OnlyIDDTO> addUser(NewUserRequest request) {

//        TODO: validate
        UserEntity newUser = new UserEntity();
        if (request.getClassId() != null && request.getClassId() > 0){
            var clazz = classRepository.findById(request.getClassId()).orElseThrow(() ->
                    new ObjectNotFoundException("classdId", request.getClassId()));
            newUser.setClazz(clazz);
        }

        if (StringUtils.isNotBlank(request.getStudentId())){
            newUser.setStudentId(request.getStudentId());
        }

        if (StringUtils.isNotBlank(request.getAvatar())){
            newUser.setAvatar(request.getAvatar());
        }

        if (StringUtils.isNotBlank(request.getPhoneNumber())){
            newUser.setPhoneNumber(request.getPhoneNumber());
        }
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        RoleEntity role = roleRepository.findById(request.getRoleId()).orElseThrow(() ->
                new ObjectNotFoundException("roleId", request.getRoleId()));
        newUser.setRole(role);
        newUser.setScore(0);
        var useAdded = userRepository.save(newUser);

        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(useAdded.getUserId())
                        .build())
                .build();
    }

    @Override
    public Response<PageDTO<UserDTO>> getListUser(GetUserRequest request) {
        var users = userDslRepository.getAllUser(RequestUtils.getPage(request.getPage()), RequestUtils.getSize(request.getSize()), request.getSort(), request.getDirection(),
                request.getClassId(), request.getRoleId(), request.getSearchTerm(), request.getFacultyId());

        return Response.<PageDTO<UserDTO>>newBuilder()
                .setSuccess(true)
                .setData(PageDTO.<UserDTO>builder()
                        .size(request.getSize())
                        .page(request.getPage())
                        .totalElements(users.getTotal())
                        .totalPages(RequestUtils.getTotalPage(users.getTotal(), request))
                        .items(users.getItems().stream()
                                .map(UserConverter::map)
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Override
    public Response<UserDTO> getUserById(Long userId) {
        var user = userRepository.findByIdAndFetchRoleFacultyAndClass(userId).orElseThrow(() ->
                new ObjectNotFoundException("userId", userId));

        return Response.<UserDTO>newBuilder()
                .setSuccess(true)
                .setData(UserConverter.map(user))
                .build();
    }

    @Override
    public Response<OnlyIDDTO> updateUser(Long userId, NewUserRequest request) {
//        TODO: validateUser

        var user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("userId", userId));

        if (request.getClassId() != null && request.getClassId() > 0){
            var clazz = classRepository.findById(request.getClassId()).orElseThrow(() ->
                    new ObjectNotFoundException("classdId", request.getClassId()));
            user.setClazz(clazz);
        }

        if (StringUtils.isNotBlank(request.getStudentId())){
            user.setStudentId(request.getStudentId());
        }

        if (StringUtils.isNotBlank(request.getAvatar())){
            user.setAvatar(request.getAvatar());
        }

        if (StringUtils.isNotBlank(request.getPhoneNumber())){
            user.setPhoneNumber(request.getPhoneNumber());
        }
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        RoleEntity role = roleRepository.findById(request.getRoleId()).orElseThrow(() ->
                new ObjectNotFoundException("roleId", request.getRoleId()));
        user.setRole(role);
        userRepository.save(user);
        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(userId)
                        .build())
                .build();
    }

    @Override
    public Response<NoContentDTO> deleteUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("userId", userId));

        userRepository.delete(user);
        return Response.<NoContentDTO>newBuilder()
                .setSuccess(true)
                .setData(NoContentDTO.builder().build())
                .build();
    }
}
