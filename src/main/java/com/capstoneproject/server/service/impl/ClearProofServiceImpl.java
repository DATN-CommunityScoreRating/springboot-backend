package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.common.enums.ClearProofStatus;
import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.common.enums.UserActivityStatus;
import com.capstoneproject.server.domain.entity.ClearProofEntity;
import com.capstoneproject.server.domain.prefetch.PrefetchEntityProvider;
import com.capstoneproject.server.domain.repository.ClearProofRepository;
import com.capstoneproject.server.domain.repository.UserActivityRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.domain.repository.dsl.ClearProofDslRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.payload.request.SendActivityRequest;
import com.capstoneproject.server.payload.request.clearProof.ClearProofRequest;
import com.capstoneproject.server.payload.request.clearProof.ConfirmClearProofRequest;
import com.capstoneproject.server.payload.request.clearProof.ListClearProofRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.ClearProofService;
import com.capstoneproject.server.util.CloudinaryUtils;
import com.capstoneproject.server.util.DateTimeUtils;
import com.capstoneproject.server.util.RequestUtils;
import com.capstoneproject.server.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@Service
@RequiredArgsConstructor
public class ClearProofServiceImpl implements ClearProofService {
    private final SecurityUtils securityUtils;
    private final ClearProofRepository clearProofRepository;
    private final UserActivityRepository userActivityRepository;
    private final PrefetchEntityProvider prefetchEntityProvider;
    private final UserRepository userRepository;
    private final CloudinaryUtils cloudinaryUtils;
    private final ClearProofDslRepository clearProofDslRepository;

    @Override
    public Response<OnlyIDDTO> sendActivityClearProof(SendActivityRequest request) {
        long userId = securityUtils.getPrincipal().getUserId();
        long activityId = request.getActivityId();
        var userActivity = userActivityRepository.findByActivityIdAndUserId(activityId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("activityId, userId", activityId));

        // TODO validate

        ClearProofEntity clearProof = new ClearProofEntity();

        clearProof.setName(request.getName());
        clearProof.setDescription(cloudinaryUtils.resolveText(request.getDescription()));
        clearProof.setUserActivityId(userActivity.getUserActivityId());
        clearProof.setStatus(prefetchEntityProvider.getClearProofStatusEntityCodeMap().get(ClearProofStatus.SEND_PROOF.status));
        clearProof.setUser(userActivity.getUser());
        var saved = clearProofRepository.save(clearProof);

        userActivity.setStatus(prefetchEntityProvider.getUserActivityStatusEntityCodeMap().get(UserActivityStatus.SEND_PROOF.status));
        userActivityRepository.save(userActivity);
        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(saved.getClearProofId())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public Response<OnlyIDDTO> confirmActivityClearProof(Long clearProofId) {
        // TODO validate
        var clearProof = clearProofRepository.findById(clearProofId).orElseThrow(() -> new ObjectNotFoundException("clearProofId", clearProofId));
        var userActivity = userActivityRepository.findById(clearProof.getUserActivityId()).get();

        userActivity.setStatus(prefetchEntityProvider.getUserActivityStatusEntityCodeMap().get(UserActivityStatus.CONFIRMED.status));
        clearProof.setStatus(prefetchEntityProvider.getClearProofStatusEntityCodeMap().get(ClearProofStatus.CONFIRMED.status));
        clearProof.setScore(userActivity.getActivity().getScore());
        var user = userActivity.getUser();
        user.setScore(user.getScore() + userActivity.getActivity().getScore());

        userActivityRepository.save(userActivity);
        clearProofRepository.save(clearProof);
        userRepository.save(user);

        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(clearProofId)
                        .build())
                .build();
    }

    @Override
    @Transactional
    public Response<OnlyIDDTO> cancelClearProofActivity(Long clearProofId) {
        // TODO validate
        var clearProof = clearProofRepository.findById(clearProofId).orElseThrow(() -> new ObjectNotFoundException("clearProofId", clearProofId));
        var userActivity = userActivityRepository.findById(clearProof.getUserActivityId()).get();

        userActivity.setStatus(prefetchEntityProvider.getUserActivityStatusEntityCodeMap().get(UserActivityStatus.NOT_ACCEPTED.status));

        clearProofRepository.delete(clearProof);
        userActivityRepository.save(userActivity);

        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(clearProofId)
                        .build())
                .build();
    }

    @Override
    public Response<OnlyIDDTO> sendClearProof(ClearProofRequest request) {
//        TODO: validate

        ClearProofEntity clearProof = new ClearProofEntity();

        var user = userRepository.findById(securityUtils.getPrincipal().getUserId()).orElseThrow(() ->
                new ObjectNotFoundException("userId", securityUtils.getPrincipal().getUserId()));

        clearProof.setName(request.getName());
        clearProof.setDescription(cloudinaryUtils.resolveText(request.getDescription()));
        clearProof.setActivityCategoryId(request.getSubCategoryId());
        clearProof.setUser(user);
        try {
            clearProof.setStartDate(DateTimeUtils.string2Timestamp(request.getStartDate()));
            clearProof.setEndDate(DateTimeUtils.string2Timestamp(request.getEndDate()));
        } catch (Exception e){
            return Response.<OnlyIDDTO>newBuilder()
                    .setSuccess(false)
                    .setErrorCode(ErrorCode.DATE_FORMAT_INVALID)
                    .build();
        }

        clearProof.setStatus(prefetchEntityProvider.getClearProofStatusEntityCodeMap().get(ClearProofStatus.SEND_PROOF.status));

        var saved = clearProofRepository.save(clearProof);
        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(saved.getClearProofId())
                        .build())
                .build();
    }

    @Override
    public Response<PageDTO<StudentClearProofDTO>> findAllClearProof(ListClearProofRequest request) {
        var principal = securityUtils.getPrincipal();
        var user = userRepository.findByIdAndFetchRoleFacultyAndClass(principal.getUserId()).get();
        var clearProofs = clearProofDslRepository.listClearProof(request, principal, user);
        return Response.<PageDTO<StudentClearProofDTO>>newBuilder()
                .setSuccess(true)
                .setData(PageDTO.<StudentClearProofDTO>builder()
                        .size(request.getSize())
                        .page(request.getPage())
                        .totalElements(clearProofs.getTotal())
                        .totalPages(RequestUtils.getTotalPage(clearProofs.getTotal(), request))
                        .items(clearProofs.getItems().stream()
                                .map(i -> StudentClearProofDTO
                                        .builder()
                                        .clearProofId(i.getClearProofId())
                                        .clearProofName(i.getName())
                                        .startDate(DateTimeUtils.timestamp2String(i.getStartDate()))
                                        .endDate(DateTimeUtils.timestamp2String(i.getEndDate()))
                                        .categoryName(prefetchEntityProvider.getActivitySubCategoryEntityMap().get(i.getActivityCategoryId()).getName())
                                        .faculty(prefetchEntityProvider.getFacultyEntityMap().get(i.getUser().getClazz().getFaculty().getFacultyId()).getFacultyName())
                                        .score(i.getUser().getScore())
                                        .studentId(i.getUser().getStudentId())
                                        .status(prefetchEntityProvider.getClearProofStatusEntityMap().get(i.getStatus().getClearProofStatusId()).getCode())
                                        .userId(i.getUser().getUserId())
                                        .studentFullName(i.getUser().getFullName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Override
    public Response<StudentClearProofDTO> findClearProofById(Long clearProofId) {
        var clearProof = clearProofRepository.findByIdAndFetchUser(clearProofId).orElseThrow(() ->
                new ObjectNotFoundException("clearProofId", clearProofId));

        var category = prefetchEntityProvider.getActivitySubCategoryEntityMap().get(clearProof.getActivityCategoryId());

        int minScore;
        int maxScore;
        var range = category.getRangeScore().split("-");
        if (range.length == 1){
            minScore = Integer.valueOf(range[0]);
            maxScore = minScore;
        } else {
            minScore = Integer.valueOf(range[0]);
            maxScore = Integer.valueOf(range[1]);
        }


        return Response.<StudentClearProofDTO>newBuilder()
                .setSuccess(true)
                .setData(StudentClearProofDTO.builder()
                        .studentFullName(clearProof.getUser().getFullName())
                        .clearProofName(clearProof.getName())
                        .clearProofId(clearProofId)
                        .studentId(clearProof.getUser().getStudentId())
                        .userId(clearProof.getUser().getUserId())
                        .clearProofScore(ObjectUtils.defaultIfNull(clearProof.getScore(), 0))
                        .categoryName(category.getName())
                        .studentFullName(clearProof.getUser().getFullName())
                        .score(clearProof.getUser().getScore())
                        .startDate(DateTimeUtils.timestamp2String(clearProof.getStartDate()))
                        .endDate(DateTimeUtils.timestamp2String(clearProof.getEndDate()))
                        .description(clearProof.getDescription())
                        .status(prefetchEntityProvider.getClearProofStatusEntityMap().get(clearProof.getStatus().getClearProofStatusId()).getCode())
                        .maxScore(maxScore)
                        .minScore(minScore)
                        .build())
                .build();
    }

    @Override
    @Transactional
    public Response<OnlyIDDTO> confirmClearProof(Long clearProofId, ConfirmClearProofRequest request) {
        // TODO validate
        var clearProof = clearProofRepository.findByIdAndFetchUser(clearProofId).orElseThrow(() ->
                new ObjectNotFoundException("clearProofId", clearProofId));

        var user = clearProof.getUser();
        if (clearProof.getStatus().getName().equals(ClearProofStatus.SEND_PROOF.status)){
            clearProof.setStatus(prefetchEntityProvider.getClearProofStatusEntityCodeMap().get(ClearProofStatus.CONFIRMED.status));
            user.setScore(user.getScore() + request.getScore());
            clearProof.setScore(request.getScore());
        }

        var saved = clearProofRepository.save(clearProof);
        userRepository.save(user);

        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(saved.getClearProofId())
                        .build())
                .build();
    }

    @Override
    public Response<OnlyIDDTO> cancelClearProof(Long clearProofId) {
        // TODO validate
        var clearProof = clearProofRepository.findById(clearProofId).orElseThrow(() ->
                new ObjectNotFoundException("clearProofId", clearProofId));
        clearProof.setStatus(prefetchEntityProvider.getClearProofStatusEntityCodeMap().get(ClearProofStatus.NOT_ACCEPTED.status));

        var saved = clearProofRepository.save(clearProof);

        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(saved.getClearProofId())
                        .build())
                .build();
    }

    @Override
    public Response<ActivityClearProofDTO> getActivityClearProof(Long userActivityId) {
        var clearProof = clearProofRepository.findByUserActivityIdAndFetchUser(userActivityId).orElseThrow(() ->
                new ObjectNotFoundException("userActivityId", userActivityId));

        var userActivity = userActivityRepository.findByIdAndFetchActivity(clearProof.getUserActivityId()).get();

        return Response.<ActivityClearProofDTO>newBuilder()
                .setSuccess(true)
                .setData(ActivityClearProofDTO.newBuilder()
                        .setActivityId(userActivity.getActivity().getActivityId())
                        .setClearProofId(clearProof.getClearProofId())
                        .setActivityName(userActivity.getActivity().getName())
                        .setScore(clearProof.getUser().getScore())
                        .setDescription(clearProof.getDescription())
                        .setStudentId(clearProof.getUser().getStudentId())
                        .setStudentFullName(clearProof.getUser().getFullName())
                        .build())
                .build();
    }
}
