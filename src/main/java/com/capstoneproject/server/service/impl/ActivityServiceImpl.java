package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.cache.JacksonRedisUtil;
import com.capstoneproject.server.common.CommunityBKDNPrincipal;
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
import com.capstoneproject.server.domain.projection.ActivityProjection;
import com.capstoneproject.server.domain.repository.ActivityRepository;
import com.capstoneproject.server.domain.repository.UserActivityRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.domain.repository.dsl.ActivityDslRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.kafka.message.CancelUserActivityMessage;
import com.capstoneproject.server.kafka.message.RegistrationActivityMessage;
import com.capstoneproject.server.kafka.producer.StudentActivityProducer;
import com.capstoneproject.server.payload.request.activity.*;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.payload.response.activity.ActivityDTO;
import com.capstoneproject.server.payload.response.activity.StudentActivityDTO;
import com.capstoneproject.server.service.ActivityService;
import com.capstoneproject.server.util.CloudinaryUtils;
import com.capstoneproject.server.util.DateTimeUtils;
import com.capstoneproject.server.util.RequestUtils;
import com.capstoneproject.server.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Duration;
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
@Log4j2
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityDslRepository activityDslRepository;
    private final SecurityUtils securityUtils;
    private final PrefetchEntityProvider prefetchEntityProvider;
    private final UserActivityRepository userActivityRepository;
    private final StudentActivityProducer studentActivityProducerService;
    private final CloudinaryUtils cloudinaryUtils;
    private final JacksonRedisUtil jacksonRedisUtil;

    @Override
    @Transactional
    public Response<OnlyIDDTO> addActivity(AddActivityRequest request) {
        var principal = securityUtils.getPrincipal();
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

        var description = cloudinaryUtils.resolveText(request.getDescription());

        ActivityEntity activity = new ActivityEntity();
        activity.setName(request.getActivityName());
        activity.setDescription(description);
        activity.setScore(request.getScore());
        activity.setMaxQuantity(request.getMaxQuantity());
        activity.setLocation(request.getLocation());
        if (principal.isFaculty()){
            var user = userRepository.findById(principal.getUserId()).get();
            activity.setFacultyId(user.getFacultyId());
        }
        activity.setCreateUserId(securityUtils.getPrincipal().getUserId());
        activity.setCreateDate(new Timestamp(System.currentTimeMillis()));
        activity.setModifyDate(new Timestamp(System.currentTimeMillis()));

        try {
            Date startDate = DateTimeUtils.move2BeginTimeOfDay(DateTimeUtils.string2Timestamp(request.getStartDate()));
            Date endDate = DateTimeUtils.move2EndTimeOfDay(DateTimeUtils.string2Timestamp(request.getEndDate()));
            Date startRegister = DateTimeUtils.move2BeginTimeOfDay(DateTimeUtils.string2Timestamp(request.getStartRegister()));
            Date endRegister = DateTimeUtils.move2EndTimeOfDay(DateTimeUtils.string2Timestamp(request.getEndRegister()));
            activity.setStartDate(new Timestamp(startDate.getTime()));
            activity.setEndDate(new Timestamp(endDate.getTime()));
            activity.setStartRegister(new Timestamp(startRegister.getTime()));
            activity.setEndRegister(new Timestamp(endRegister.getTime()));
        } catch (ParseException e){
            e.printStackTrace();
            return Response.<OnlyIDDTO>newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .setErrorCode(ErrorCode.INTERNAL_ERROR)
                    .build();
        }

        var savedActivity = activityRepository.save(activity);
        jacksonRedisUtil.put(String.format("activity_%s", savedActivity.getActivityId()), new ArrayList<Long>(), Duration.ofMillis(activity.getEndRegister().getTime() - activity.getStartRegister().getTime()));

        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(savedActivity.getActivityId())
                        .build())
                .build();
    }

    @Override
    public Response<PageDTO<ActivityDTO>> listActivity(ListActivitiesRequest request) {
        var principal = securityUtils.getPrincipal();
        var userEntity = userRepository.findByIdAndFetchRoleFacultyAndClass(principal.getUserId()).get();
        var activityPage = activityDslRepository.listActivity(request, principal, principal.isStudent() ? userEntity.getClazz().getFaculty().getFacultyId() : null);

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
                                        .setStartRegister(DateTimeUtils.timestamp2String(i.getStartRegister()))
                                        .setEndRegister(DateTimeUtils.timestamp2String(i.getEndRegister()))
                                        .setTotalParticipant(Math.toIntExact(i.getTotalParticipant()))
                                        .setOrganization(getOrganization(i.getCreateUserId()))
                                        .setStatus(getActivityStatus(i.getStartDate(), i.getEndDate(), i.getStartRegister(), i.getEndRegister(), Math.toIntExact(i.getTotalParticipant()), i.getMaxQuantity()))
                                        .setRegistered(getRegistered(i, principal.getUserId()))
                                        .setNeedConfirmation(!principal.isStudent() && i.getNeedConfirmation())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    private Boolean getRegistered(ActivityProjection projection, Long userId) {
        List<Long> userRegistered = jacksonRedisUtil.getAsList(String.format("activity_%s", projection.getActivityId()), Long.class);
        if (userRegistered == null){
            return projection.getRegistered();
        }
        return userRegistered.contains(userId);
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
                        .setStatus(getActivityStatus(activity.getStartDate(), activity.getEndDate(), activity.getStartRegister(), activity.getEndRegister(), Math.toIntExact(totalParticipant), activity.getMaxQuantity()))
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

    @Override
    public Response<PageDTO<ActivityDTO>> myActivity(MyActivityRequest request) {
        CommunityBKDNPrincipal principal = securityUtils.getPrincipal();
        var myActivity = activityDslRepository.findMyActivity(request, principal.getUserId());

        return Response.<PageDTO<ActivityDTO>>newBuilder()
                .setSuccess(true)
                .setData(PageDTO.<ActivityDTO>builder()
                        .page(request.getPage())
                        .size(request.getSize())
                        .totalElements(myActivity.getTotal())
                        .totalPages(RequestUtils.getTotalPage(myActivity.getTotal(), request))
                        .items(myActivity.getItems().stream()
                                .map(i -> ActivityDTO.newBuilder()
                                        .setActivityId(i.getActivity().getActivityId())
                                        .setScore(i.getActivity().getScore())
                                        .setMaxQuantity(i.getActivity().getMaxQuantity())
                                        .setLocation(i.getActivity().getLocation())
                                        .setName(i.getActivity().getName())
                                        .setStartDate(DateTimeUtils.timestamp2String(i.getActivity().getStartDate()))
                                        .setEndDate(DateTimeUtils.timestamp2String(i.getActivity().getEndDate()))
                                        .setStartRegister(DateTimeUtils.timestamp2String(i.getActivity().getStartRegister()))
                                        .setEndRegister(DateTimeUtils.timestamp2String(i.getActivity().getEndRegister()))
                                        .setOrganization(getOrganization(i.getActivity().getCreateUserId()))
                                        .setUserActivityStatus(prefetchEntityProvider.getUserActivityStatusEntityMap().get(i.getStatus().getUserActivityStatusId()).getStatus())
                                        .setStatus(getActivityStatus(i.getActivity().getStartDate(), i.getActivity().getEndDate(), i.getActivity().getStartRegister(), i.getActivity().getEndRegister(), -1, i.getActivity().getMaxQuantity()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Override
    public Response<NoContentDTO> cancelActivity(Long activityId) {
        var principal = securityUtils.getPrincipal();
        var userActivity = userActivityRepository.findByActivityIdAndUserId(activityId, principal.getUserId())
                .orElseThrow(() -> new ObjectNotFoundException("activityId", activityId));
        return deleteUserActivity(userActivity.getUserActivityId());
    }

    @Override
    public Response<OnlyIDDTO> registrationActivityKafka(RegistrationActivityRequest request) {
        var userId = securityUtils.getPrincipal().getUserId();
        var activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new ObjectNotFoundException("activityId", request.getActivityId()));

        ErrorCode code = null;
        Date now = new Date();
        if (activity.getStartRegister().after(now) || activity.getEndRegister().before(now)) {
            code = ErrorCode.OUTSIDE_REGISTRATION_PERIOD;
        }

        List<Long> userRegisted = null;

        List<Long> userRegistered = jacksonRedisUtil.getAsList(String.format("activity_%s", activity.getActivityId()), Long.class);

        if (userRegistered != null){
            if (code == null && userRegistered.contains(userId)){
                code = ErrorCode.ALREADY_EXIST;
            }

            if (code == null && userRegistered.size() >= activity.getMaxQuantity()){
                code = ErrorCode.ENOUGH_QUANTITY;
            }
        } else {
            userRegisted = userActivityRepository.getAllUserIdRegistedActivity(request.getActivityId());
            if (code == null && userRegisted.contains(userId)){
                code = ErrorCode.ALREADY_EXIST;
            }

            if (code == null && userRegisted.size() >= activity.getMaxQuantity()){
                code = ErrorCode.ENOUGH_QUANTITY;
            }
        }


        if (code != null) {
            return Response.<OnlyIDDTO>newBuilder()
                    .setSuccess(false)
                    .setErrorCode(code)
                    .setMessage("Can't not registration activity")
                    .build();
        }

        RegistrationActivityMessage message = new RegistrationActivityMessage();
        message.setActivityId(activity.getActivityId());
        message.setUserId(userId);

        studentActivityProducerService.studentRegistration(message);
        if (userRegistered != null){
            userRegistered.add(userId);
            jacksonRedisUtil.put(String.format("activity_%s", activity.getActivityId()), userRegistered, Duration.ofMillis(activity.getEndRegister().getTime() - activity.getStartRegister().getTime()));

        } else {
            if (!userRegisted.contains(userId)){
                userRegisted.add(userId);
            }
            jacksonRedisUtil.put(String.format("activity_%s", activity.getActivityId()), userRegisted, Duration.ofMillis(activity.getEndRegister().getTime() - activity.getStartRegister().getTime()));
        }
        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(activity.getActivityId())
                        .build())
                .build();
    }

    @Override
    public Response<NoContentDTO> cancelActivityKafka(Long activityId) {
        var principal = securityUtils.getPrincipal();
        var activity = activityRepository.findById(activityId).orElseThrow(() ->
                new ObjectNotFoundException("activityId", activityId));
        List<Long> userRegistered = jacksonRedisUtil.getAsList(String.format("activity_%s", activity.getActivityId()), Long.class);
        if (userRegistered != null && !userRegistered.contains(principal.getUserId())){
            throw new ObjectNotFoundException("activityId", activityId);
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());

//        TODO: Validate
        if (activity.getStartDate().before(now)){
            return Response.<NoContentDTO>newBuilder()
                    .setSuccess(false)
                    .setErrorCode(ErrorCode.ACTIVITY_GOING_ON)
                    .setMessage("Can't not update activity going on")
                    .build();
        }

        if (userRegistered != null){
            userRegistered = userRegistered.stream().filter(i -> !i.equals(principal.getUserId())).collect(Collectors.toList());
        }

        if (userRegistered == null || userRegistered.isEmpty()){
            jacksonRedisUtil.evict(String.format("activity_%s", activity.getActivityId()), Long.class);
        } else {
            jacksonRedisUtil.put(String.format("activity_%s", activity.getActivityId()), userRegistered, Duration.ofMillis(activity.getEndRegister().getTime() - activity.getStartRegister().getTime()));

        }

        studentActivityProducerService.cancelRegistrationActivity(CancelUserActivityMessage.builder()
                        .activityId(activity.getActivityId())
                        .userId(principal.getUserId())
                .build());
        return Response.<NoContentDTO>newBuilder()
                .setSuccess(true)
                .setData(NoContentDTO.builder().build())
                .build();
    }

    private String getActivityStatus(Timestamp startDate, Timestamp endDate, Timestamp startRegister, Timestamp endRegister, int totalParticipant, int maxQuantity) {
        Date now = new Date();

        if (now.after(new Date(startDate.getTime())) && now.before(new Date(endDate.getTime()))){
            return ActivityStatus.GOING_ON.name();
        }

        if (now.after(new Date(endRegister.getTime())) && now.before(new Date(startDate.getTime()))){
            return ActivityStatus.IS_COMING.name();
        }

        if (now.after(new Date(endDate.getTime()))){
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
