package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.domain.entity.ActivityEntity;
import com.capstoneproject.server.domain.repository.ActivityRepository;
import com.capstoneproject.server.domain.repository.dsl.ActivityDslRepository;
import com.capstoneproject.server.payload.request.activity.AddActivityRequest;
import com.capstoneproject.server.payload.request.activity.ListActivitiesRequest;
import com.capstoneproject.server.payload.response.ErrorDTO;
import com.capstoneproject.server.payload.response.OnlyIDDTO;
import com.capstoneproject.server.payload.response.PageDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.payload.response.activity.ActivityDTO;
import com.capstoneproject.server.service.ActivityService;
import com.capstoneproject.server.util.DateTimeUtils;
import com.capstoneproject.server.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
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
    private final ActivityDslRepository activityDslRepository;
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
        var activityPage = activityDslRepository.listActivity(request);


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
                                        .setOrganization("")
                                        .setStatus("")
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
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
