package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.domain.entity.ActivityCategoryEntity;
import com.capstoneproject.server.domain.entity.ActivitySubCategoryEntity;
import com.capstoneproject.server.domain.repository.ActivityCategoryRepository;
import com.capstoneproject.server.domain.repository.ActivitySubCategoryRepository;
import com.capstoneproject.server.payload.response.ActivityCategoryDTO;
import com.capstoneproject.server.payload.response.ActivitySubCategoryDTO;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.service.ActivityCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */

@Service
@RequiredArgsConstructor
public class ActivityCategoryServiceImpl implements ActivityCategoryService {
    private final ActivitySubCategoryRepository activitySubCategoryRepository;
    private final ActivityCategoryRepository activityCategoryRepository;

    @Override
    public Response<ListDTO<ActivityCategoryDTO>> findAllActivityCategory() {
        var list = activityCategoryRepository.findAll();

        return Response.<ListDTO<ActivityCategoryDTO>>newBuilder()
                .setSuccess(true)
                .setData(ListDTO.<ActivityCategoryDTO>builder()
                        .totalElements((long) list.size())
                        .items(list.stream().map(i -> ActivityCategoryDTO.builder().activityCategoryId(i.getActivityCategoryId())
                                .name(i.getName())
                                .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Override
    public Response<ListDTO<ActivitySubCategoryDTO>> findAllActivitySubCategory(Long activityCategoryId) {
        List<ActivitySubCategoryEntity> list;
        if (activityCategoryId != null) {
            list = activitySubCategoryRepository.findAllByActivityCategoryId(activityCategoryId);
        } else {
            list = activitySubCategoryRepository.findAll();
        }
        return Response.<ListDTO<ActivitySubCategoryDTO>>newBuilder()
                .setSuccess(true)
                .setData(ListDTO.<ActivitySubCategoryDTO>builder()
                        .totalElements((long) list.size())
                        .items(list.stream().map(i -> {
                            int minScore;
                            int maxScore;
                            var range = i.getRangeScore().split("-");
                            if (range.length == 1){
                                minScore = Integer.valueOf(range[0]);
                                maxScore = minScore;
                            } else {
                                minScore = Integer.valueOf(range[0]);
                                maxScore = Integer.valueOf(range[1]);
                            }
                            return ActivitySubCategoryDTO.builder()
                                    .activitySubCategoryId(i.getActivitySubCategoryId())
                                    .name(i.getName())
                                    .activityCategoryId(i.getActivityCategoryId())
                                    .minScore(minScore)
                                    .maxScore(maxScore)
                                    .build();
                                })
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }
}
