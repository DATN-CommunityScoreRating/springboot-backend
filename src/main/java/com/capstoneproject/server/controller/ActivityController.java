package com.capstoneproject.server.controller;

import com.capstoneproject.server.payload.request.activity.AddActivityRequest;
import com.capstoneproject.server.payload.request.activity.ListActivitiesRequest;
import com.capstoneproject.server.payload.response.OnlyIDDTO;
import com.capstoneproject.server.payload.response.PageDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.payload.response.activity.ActivityDTO;
import com.capstoneproject.server.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */


@RestController
@RequiredArgsConstructor
@Tag(name = "Activity APIs")
@RequestMapping("activity")
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping
    @Operation(summary = "Add activity")
    public Response<OnlyIDDTO> addActivity(@RequestBody AddActivityRequest request){
        return activityService.addActivity(request);
    }

    @GetMapping
    @Operation(summary = "List activity")
    public Response<PageDTO<ActivityDTO>> listActivities(@ModelAttribute ListActivitiesRequest request){
        return activityService.listActivity(request);
    }

}
