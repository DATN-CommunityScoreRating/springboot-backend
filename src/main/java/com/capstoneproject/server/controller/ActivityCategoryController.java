package com.capstoneproject.server.controller;

import com.capstoneproject.server.payload.response.ActivityCategoryDTO;
import com.capstoneproject.server.payload.response.ActivitySubCategoryDTO;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.service.ActivityCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */

@RestController
@RequiredArgsConstructor
public class ActivityCategoryController {
    private final ActivityCategoryService activityCategoryService;
    @GetMapping("activity-categories")
    public Response<ListDTO<ActivityCategoryDTO>> findAllActivityCategory(){
        return activityCategoryService.findAllActivityCategory();
    }

    @GetMapping("activity-subcategories")
    public Response<ListDTO<ActivitySubCategoryDTO>> findAllActivityCategory(@RequestParam(name = "activityCategoryId", required = false) Long activityCategoryId){
        return activityCategoryService.findAllActivitySubCategory(activityCategoryId);
    }
}
