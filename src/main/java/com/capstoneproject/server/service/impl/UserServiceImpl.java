package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.cache.JacksonRedisUtil;
import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.converter.UserConverter;
import com.capstoneproject.server.domain.dto.UploadStudentCSV;
import com.capstoneproject.server.domain.entity.ClassEntity;
import com.capstoneproject.server.domain.entity.RoleEntity;
import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.domain.prefetch.PrefetchEntityProvider;
import com.capstoneproject.server.domain.repository.ClassRepository;
import com.capstoneproject.server.domain.repository.FacultyRepository;
import com.capstoneproject.server.domain.repository.RoleRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.domain.repository.dsl.UserDslRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.payload.request.ImportRequest;
import com.capstoneproject.server.payload.request.user.GetUserRequest;
import com.capstoneproject.server.payload.request.user.NewUserRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.UserService;
import com.capstoneproject.server.util.RequestUtils;
import com.capstoneproject.server.util.SecurityUtils;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
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
    private final JacksonRedisUtil jacksonRedisUtil;
    private final SecurityUtils securityUtils;
    private final FacultyRepository facultyRepository;
    private final PrefetchEntityProvider prefetchEntityProvider;

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
        var principal = securityUtils.getPrincipal();

        if (principal.isClass()){
            var user = userRepository.findById(principal.getUserId()).get();
            request.setClassId(user.getClazz().getClassId());
        }

        if (principal.isUnion() || principal.isFaculty()){
            var user = userRepository.findById(principal.getUserId()).get();
            request.setFacultyId(user.getFacultyId());
        }
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

    @Override
    public Response<UploadDTO<UploadStudentDTO>> uploadStudent(MultipartFile request) {
        try (Reader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))){
            CsvToBean<UploadStudentCSV> csvToBean = new CsvToBeanBuilder<UploadStudentCSV>(reader)
                    .withSkipLines(1)
                    .withType(UploadStudentCSV.class)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<UploadStudentCSV> records = csvToBean.parse();
            List<UploadStudentDTO> result = new ArrayList<>();
            var listStudentIds = records.stream().map(UploadStudentCSV::getStudentId).collect(Collectors.toUnmodifiableList());
            var listUsernames = records.stream().map(UploadStudentCSV::getUsername).collect(Collectors.toUnmodifiableList());
            var listEmails = records.stream().map(UploadStudentCSV::getEmail).collect(Collectors.toUnmodifiableList());
            var listClass = records.stream().map(UploadStudentCSV::getClassName).collect(Collectors.toUnmodifiableList());

            var studentIdExist = userRepository.findByStudentIds(listStudentIds)
                            .stream().map(UserEntity::getStudentId).collect(Collectors.toUnmodifiableList());
            var emailExist = userRepository.findByEmails(listEmails)
                            .stream().map(UserEntity::getEmail).collect(Collectors.toUnmodifiableList());
            var usernameExist = userRepository.findByUsernames(listUsernames)
                            .stream().map(UserEntity::getUsername).collect(Collectors.toUnmodifiableList());
            var classExist = classRepository.findAllByClassNames(listClass)
                            .stream().collect(Collectors.toMap(ClassEntity::getClassName, c -> c));

            records.forEach(record -> {
                var uploadStudentBuilder = UploadStudentDTO.builder()
                        .studentId(record.getStudentId())
                        .className(record.getClassName())
                        .username(record.getUsername())
                        .password(record.getPassword())
                        .email(record.getEmail())
                        .fullName(record.getFullName())
                        .phoneNumber(record.getPhoneNumber());
                // TODO: validate
                Map<String, String> errors = new HashMap<>();

                if (studentIdExist.contains(record.getStudentId())){
                    errors.put("studentId", ErrorCode.ALREADY_EXIST.name());
                }

                if (usernameExist.contains(record.getUsername())){
                    errors.put("username", ErrorCode.ALREADY_EXIST.name());
                }

                if (emailExist.contains(record.getEmail())){
                    errors.put("email", ErrorCode.ALREADY_EXIST.name());
                }

                if (!classExist.containsKey(record.getClassName())){
                    errors.put("className", ErrorCode.NOT_FOUND.name());
                }

                uploadStudentBuilder.errors(errors);

                result.add(uploadStudentBuilder.build());
            });

            UUID uuid = UUID.randomUUID();

            jacksonRedisUtil.put(String.format("%s_%s", securityUtils.getPrincipal().getUserId(), uuid), result,
                    JacksonRedisUtil.DEFAULT_DURATION);
            return Response.<UploadDTO<UploadStudentDTO>>newBuilder()
                    .setSuccess(true)
                    .setData(UploadDTO.<UploadStudentDTO>builder()
                            .totalElements((long) records.size())
                            .items(result)
                            .correlationId(uuid)
                            .build())
                    .build();
        } catch (IOException e){
            e.printStackTrace();
            return Response.<UploadDTO<UploadStudentDTO>>newBuilder()
                    .setSuccess(false)
                    .setErrorCode(ErrorCode.IO_ERROR)
                    .build();
        }
    }

    @Override
    @Transactional
    public Response<ImportDTO> importStudent(ImportRequest request) {
        var studentList = jacksonRedisUtil.getAsList(String.format("%s_%s",securityUtils.getPrincipal().getUserId(), request.getCorrelationId()), UploadStudentDTO.class);
        var total = studentList.size();
        var successList = studentList.stream().filter(s -> s.getErrors() == null || s.getErrors().isEmpty()).collect(Collectors.toList());
        var classExist = classRepository.findAllByClassNames(successList.stream().map(s -> s.getClassName()).collect(Collectors.toUnmodifiableList()))
                .stream().collect(Collectors.toMap(ClassEntity::getClassName, c -> c));
        List<UserEntity> students = new ArrayList<>();
        for (var student: successList) {
            UserEntity user = new UserEntity();
            user.setStudentId(student.getStudentId());
            user.setUsername(student.getUsername());
            user.setPassword(passwordEncoder.encode(student.getPassword()));
            user.setScore(0);
            user.setEmail(student.getEmail());
            user.setPhoneNumber(student.getPhoneNumber());
            user.setFullName(student.getFullName());
            user.setClazz(classExist.get(student.getClassName()));
            user.setRole(prefetchEntityProvider.getRoleEntityNameMap().get(CommunityBKDNPermission.Role.STUDENT));
            students.add(user);
        }
        if (!students.isEmpty()){
            userRepository.saveAll(students);
        }
        return Response.<ImportDTO>newBuilder()
                .setSuccess(true)
                .setData(ImportDTO.builder()
                        .total(total)
                        .totalSuccess(successList.size())
                        .totalError(total - successList.size())
                        .build())
                .build();
    }

    @Override
    public Response<UserDTO> getMyAccount() {
        var principal = securityUtils.getPrincipal();
        var user = userRepository.findByIdAndFetchRoleFacultyAndClassCourse(securityUtils.getPrincipal().getUserId()).orElseThrow();

        var responseBuilder = UserDTO.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .score(user.getScore());
        if (principal.isFaculty() || principal.isUnion() || principal.isClass()){
            responseBuilder.faculty(prefetchEntityProvider.getFacultyEntityMap().get(user.getFacultyId()).getFacultyName());
        }

        if (principal.isClass() || principal.isStudent()){
            responseBuilder.course(prefetchEntityProvider.getCourseEntityMap().get(user.getClazz().getCourseEntity().getCourseId()).getName())
                    .className(user.getClazz().getClassName());
        }

        if (principal.isStudent()){
            responseBuilder.studentId(user.getStudentId());
            responseBuilder.faculty(prefetchEntityProvider.getFacultyEntityMap().get(user.getClazz().getFaculty().getFacultyId()).getFacultyName());
        }
        return Response.<UserDTO>newBuilder()
                .setSuccess(true)
                .setData(responseBuilder.build())
                .build();
    }

}
