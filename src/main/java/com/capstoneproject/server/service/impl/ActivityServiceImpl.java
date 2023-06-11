package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.common.constants.Constant;
import com.capstoneproject.server.common.enums.ActivityStatus;
import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.common.enums.UserActivityStatus;
import com.capstoneproject.server.converter.UserConverter;
import com.capstoneproject.server.domain.entity.ActivityEntity;
import com.capstoneproject.server.domain.entity.UserActivityEntity;
import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.domain.prefetch.PrefetchEntityProvider;
import com.capstoneproject.server.domain.repository.ActivityRepository;
import com.capstoneproject.server.domain.repository.UserActivityRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.domain.repository.dsl.ActivityDslRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.payload.request.activity.AddActivityRequest;
import com.capstoneproject.server.payload.request.activity.ListActivitiesRequest;
import com.capstoneproject.server.payload.request.activity.RegistrationActivityRequest;
import com.capstoneproject.server.payload.request.activity.UserActivityRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.payload.response.activity.ActivityDTO;
import com.capstoneproject.server.payload.response.activity.StudentActivityDTO;
import com.capstoneproject.server.service.ActivityService;
import com.capstoneproject.server.util.DateTimeUtils;
import com.capstoneproject.server.util.RequestUtils;
import com.capstoneproject.server.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityDslRepository activityDslRepository;
    private final SecurityUtils securityUtils;
    private final PrefetchEntityProvider prefetchEntityProvider;
    private final UserActivityRepository userActivityRepository;

    @Override
    public Response<OnlyIDDTO> addActivity(AddActivityRequest request) {
        List<ErrorDTO> errors = new ArrayList<>();
        validateActivity(request, errors);

        if (!errors.isEmpty()){
            return Response.<OnlyIDDTO>newBuilder()
                    .setSuccess(false)
                    .setErrors(errors)
                    .setErrorCode(ErrorCode.BAD_REQUEST)
                    .setMessage("Validate failure")
                    .build();
        }

        ActivityEntity activity = new ActivityEntity();
        activity.setName(request.getActivityName());
        activity.setDescription(request.getDescription());
        activity.setScore(request.getScore());
        activity.setMaxQuantity(request.getMaxQuantity());
        activity.setLocation(request.getLocation());
        activity.setCreateUserId(securityUtils.getPrincipal().getUserId());
        activity.setCreateDate(new Timestamp(System.currentTimeMillis()));
        activity.setModifyDate(new Timestamp(System.currentTimeMillis()));

        try {
            activity.setStartDate(DateTimeUtils.string2Timestamp(request.getStartDate()));
            activity.setEndDate(DateTimeUtils.string2Timestamp(request.getEndDate()));
            activity.setStartRegister(DateTimeUtils.string2Timestamp(request.getStartRegister()));
            activity.setEndRegister(DateTimeUtils.string2Timestamp(request.getEndRegister()));
        } catch (ParseException e){
            e.printStackTrace();
            return Response.<OnlyIDDTO>newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .setErrorCode(ErrorCode.INTERNAL_ERROR)
                    .build();
        }

        var savedActivity = activityRepository.save(activity);

        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(savedActivity.getActivityId())
                        .build())
                .build();
    }

    @Override
    public Response<PageDTO<ActivityDTO>> listActivity(ListActivitiesRequest request) {
        Long userId = securityUtils.getPrincipal().getUserId();
        var activityPage = activityDslRepository.listActivity(request, userId);

        return Response.<PageDTO<ActivityDTO>>newBuilder()
                .setSuccess(true)
                .setData(PageDTO.<ActivityDTO>builder()
                        .page(request.getPage())
                        .size(request.getSize())
                        .totalElements(activityPage.getTotal())
                        .totalPages(RequestUtils.getTotalPage(activityPage.getTotal(), request))
                        .items(activityPage.getItems().stream()
                                .map(i -> ActivityDTO.newBuilder()
                                        .setActivityId(i.getActivityId())
                                        .setScore(i.getScore())
                                        .setMaxQuantity(i.getMaxQuantity())
                                        .setLocation(i.getLocation())
                                        .setName(i.getActivityName())
                                        .setStartDate(DateTimeUtils.timestamp2String(i.getStartDate()))
                                        .setEndDate(DateTimeUtils.timestamp2String(i.getEndDate()))
                                        .setTotalParticipant(Math.toIntExact(i.getTotalParticipant()))
                                        .setOrganization(getOrganization(i.getCreateUserId()))
                                        .setStatus(getActivityStatus(i.getStartRegister(), i.getEndRegister(), Math.toIntExact(i.getTotalParticipant()), i.getMaxQuantity()))
                                        .setRegistered(i.getRegistered())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Override
    public Response<OnlyIDDTO> registrationActivity(RegistrationActivityRequest request) {
        var userId = securityUtils.getPrincipal().getUserId();
        var activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new ObjectNotFoundException("activityId", request.getActivityId()));

        ErrorCode code = null;
        Date now = new Date();
        if (activity.getStartRegister().after(now) || activity.getEndRegister().before(now)) {
            code = ErrorCode.OUTSIDE_REGISTRATION_PERIOD;
        }

        List<Long> userRegisted = userActivityRepository.getAllUserIdRegistedActivity(request.getActivityId());

        if (code == null && userRegisted.contains(userId)){
            code = ErrorCode.ALREADY_EXIST;
        }

        if (code == null && userRegisted.size() >= activity.getMaxQuantity()){
            code = ErrorCode.ENOUGH_QUANTITY;
        }

        if (code != null) {
            return Response.<OnlyIDDTO>newBuilder()
                    .setSuccess(false)
                    .setErrorCode(code)
                    .setMessage("Can't not registration activity")
                    .build();
        }
        var user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("userId", userId));

        UserActivityEntity userActivity = new UserActivityEntity();
        userActivity.setUser(user);
        userActivity.setActivity(activity);
        userActivity.setStatus(prefetchEntityProvider.getUserActivityStatusEntityCodeMap().get(UserActivityStatus.REGISTERED.name()));
        var userActivitySaved = userActivityRepository.save(userActivity);
        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(userActivitySaved.getUserActivityId())
                        .build())
                .build();
    }

    @Override
    public Response<PageDTO<StudentActivityDTO>> getUserRegisterActivity(Long activityId, UserActivityRequest request) {
        var users = activityDslRepository.listUserActivity(activityId, request);
        return  Response.<PageDTO<StudentActivityDTO>>newBuilder()
                .setSuccess(true)
                .setData(PageDTO.<StudentActivityDTO>builder()
                        .page(request.getPage())
                        .size(request.getSize())
                        .totalElements(users.getTotal())
                        .totalPages(RequestUtils.getTotalPage(users.getTotal(), request))
                        .items(users.getItems().stream()
                                .map(item -> {
                                    var user = item.get(0, UserEntity.class);
                                    var userActivity = item.get(1, UserActivityEntity.class);
                                    return UserConverter.map(user, userActivity);
                                }).collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Override
    public Response<ActivityDTO> findById(Long activityId) {
        var tuples = activityRepository.findByIdAndFetchUser(activityId).orElseThrow(() ->
                new ObjectNotFoundException("activityId", activityId));
        ActivityEntity activity = null;
        Long totalParticipant = 0L;
        for (var tuple : tuples){
            activity = tuple.get(0, ActivityEntity.class);
            totalParticipant = tuple.get(1, Long.class);
        }

        return Response.<ActivityDTO>newBuilder()
                .setSuccess(true)
                .setData(ActivityDTO.newBuilder()
                        .setActivityId(activity.getActivityId())
                        .setScore(activity.getScore())
                        .setMaxQuantity(activity.getMaxQuantity())
                        .setLocation(activity.getLocation())
                        .setName(activity.getName())
                        .setStartDate(DateTimeUtils.timestamp2String(activity.getStartDate()))
                        .setEndDate(DateTimeUtils.timestamp2String(activity.getEndDate()))
                        .setTotalParticipant(Math.toIntExact(totalParticipant))
                        .setOrganization(getOrganization(activity.getCreateUserId()))
                        .setStatus(getActivityStatus(activity.getStartRegister(), activity.getEndRegister(), Math.toIntExact(totalParticipant), activity.getMaxQuantity()))
                        .setDescription(activity.getDescription())
                        .build())
                .build();
    }

    @Override
    public Response<NoContentDTO> deleteUserActivity(Long userActivityId) {
        var userActivity = userActivityRepository.findByIdAndFetchActivity(userActivityId).orElseThrow(() ->
                new ObjectNotFoundException("userActivityId", userActivityId));

        Timestamp now = new Timestamp(System.currentTimeMillis());

//        TODO: Validate
        if (userActivity.getActivity().getStartDate().before(now)){
            return Response.<NoContentDTO>newBuilder()
                    .setSuccess(false)
                    .setErrorCode(ErrorCode.ACTIVITY_GOING_ON)
                    .setMessage("Can't not update activity going on")
                    .build();
        }

        userActivityRepository.delete(userActivity);
        return Response.<NoContentDTO>newBuilder()
                .setSuccess(true)
                .setData(NoContentDTO.builder().build())
                .build();
    }

    @Override
    public Response<NoContentDTO> deleteActivity(Long activityId) {
        var activity = activityRepository.findById(activityId).orElseThrow(() ->
            new ObjectNotFoundException("activityId", activityId));

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (activity.getStartDate().before(now)){
            return Response.<NoContentDTO>newBuilder()
                    .setSuccess(false)
                    .setErrorCode(ErrorCode.ACTIVITY_GOING_ON)
                    .setMessage("Can't not update activity going on")
                    .build();
        }

        var listUserActivity = userActivityRepository.findAllByActivityId(activityId);
        userActivityRepository.deleteAll(listUserActivity);
        activityRepository.delete(activity);
        return Response.<NoContentDTO>newBuilder()
                .setSuccess(true)
                .setData(NoContentDTO.builder().build())
                .build();
    }

    private String getActivityStatus(Timestamp startRegister, Timestamp endRegister, int totalParticipant, int maxQuantity) {
        Date now = new Date();

        if (now.after(new Date(endRegister.getTime()))){
            return ActivityStatus.EXPIRED.name();
        }

        if (now.before(new Date(startRegister.getTime()))){
            return ActivityStatus.PENDING.name();
        }

        if (totalParticipant >= maxQuantity){
            return ActivityStatus.FULLY.name();
        }

        return ActivityStatus.ACTIVE.name();
    }

    private String getOrganization(Long createUserId) {
        var createUser = userRepository.findById(createUserId).orElseThrow(() ->
                new ObjectNotFoundException("userId", createUserId));

        var role = prefetchEntityProvider.getRoleEntityMap().get(createUser.getRole().getRoleId());
        String organization = Constant.DAI_HOC_BACH_KHOA;

        switch (role.getRoleName()){
            case CommunityBKDNPermission.Role.ADMIN:
                organization = Constant.DAI_HOC_BACH_KHOA;
                break;
            case CommunityBKDNPermission.Role.YOUTH_UNION:
                organization = Constant.DOAN_THANH_NIEN;
                break;
            case CommunityBKDNPermission.Role.FACULTY:
                var faculty = prefetchEntityProvider.getFacultyEntityMap().get(createUser.getFacultyId());
                organization = faculty.getFacultyName();
                break;
            case CommunityBKDNPermission.Role.UNION:
                var faculty1 = prefetchEntityProvider.getFacultyEntityMap().get(createUser.getFacultyId());
                organization = Constant.LIEN_CHI_DOAN + " " + faculty1.getFacultyName();
            default:
                break;
        }

        return organization;
    }

    private void validateActivity(AddActivityRequest request, List<ErrorDTO> errors){
        if (StringUtils.isBlank(request.getActivityName())){
            errors.add(ErrorDTO.of("activityName", ErrorCode.NOT_EMPTY));
        }

        if (StringUtils.isBlank(request.getDescription())){
            errors.add(ErrorDTO.of("description", ErrorCode.NOT_EMPTY));
        }

        if (StringUtils.isBlank(request.getLocation())){
            errors.add(ErrorDTO.of("location", ErrorCode.NOT_EMPTY));
        }

        if (StringUtils.isBlank(request.getStartDate())){
            errors.add(ErrorDTO.of("startDate", ErrorCode.NOT_EMPTY));
        }

        if (StringUtils.isBlank(request.getEndDate())){
            errors.add(ErrorDTO.of("endDate", ErrorCode.NOT_EMPTY));
        }

        if (StringUtils.isBlank(request.getStartRegister())){
            errors.add(ErrorDTO.of("startRegister", ErrorCode.NOT_EMPTY));
        }

        if (StringUtils.isBlank(request.getEndRegister())){
            errors.add(ErrorDTO.of("endRegister", ErrorCode.NOT_EMPTY));
        }

        if (request.getScore() > 30 || request.getScore() < 5){
            errors.add(ErrorDTO.of("score", ErrorCode.INVALID_VALUE));
        }

        if (request.getMaxQuantity() < 0){
            errors.add(ErrorDTO.of("quantity", ErrorCode.INVALID_VALUE));
        }

        try {
            DateTimeUtils.string2Timestamp(request.getStartDate());
        } catch (ParseException e){
            errors.add(ErrorDTO.of("startDate", ErrorCode.INVALID_VALUE));
        }

        try {
            DateTimeUtils.string2Timestamp(request.getEndDate());
        } catch (ParseException e){
            errors.add(ErrorDTO.of("endDate", ErrorCode.INVALID_VALUE));
        }

        try {
            DateTimeUtils.string2Timestamp(request.getStartRegister());
        } catch (ParseException e){
            errors.add(ErrorDTO.of("startRegister", ErrorCode.INVALID_VALUE));
        }

        try {
            DateTimeUtils.string2Timestamp(request.getEndRegister());
        } catch (ParseException e){
            errors.add(ErrorDTO.of("endRegister", ErrorCode.INVALID_VALUE));
        }
    }
}
