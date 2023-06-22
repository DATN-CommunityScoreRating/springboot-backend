package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.response.ActivityCategoryDTO;
import com.capstoneproject.server.payload.response.ActivitySubCategoryDTO;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.Response;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */

public interface ActivityCategoryService {
    Response<ListDTO<ActivityCategoryDTO>> findAllActivityCategory();

    Response<ListDTO<ActivitySubCategoryDTO>> findAllActivitySubCategory(Long activityCategoryId);
}
