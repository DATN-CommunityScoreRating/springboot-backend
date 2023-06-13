package com.capstoneproject.server.controller;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.payload.request.activity.*;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.payload.response.activity.ActivityDTO;
import com.capstoneproject.server.payload.response.activity.StudentActivityDTO;
import com.capstoneproject.server.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */


@RestController
@RequiredArgsConstructor
@Tag(name = "Activity APIs")
@RequestMapping("activities")
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN,
            CommunityBKDNPermission.Role.FACULTY,
            CommunityBKDNPermission.Role.UNION,
            CommunityBKDNPermission.Role.YOUTH_UNION
    })
    @Operation(summary = "Add activity")
    public Response<OnlyIDDTO> addActivity(@RequestBody AddActivityRequest request){
        return activityService.addActivity(request);
    }

    @GetMapping
    @Operation(summary = "List activity")
    public Response<PageDTO<ActivityDTO>> listActivities(@ModelAttribute ListActivitiesRequest request){
        return activityService.listActivity(request);
    }

    @Operation(summary = "Student registration acvitity")
    @Secured(value = {
            CommunityBKDNPermission.Role.STUDENT
    })
    @PostMapping("registration")
    public Response<OnlyIDDTO> registrationActivity(@RequestBody RegistrationActivityRequest request){
        return activityService.registrationActivity(request);
    }

    @GetMapping("{id}/students")
    @Operation(summary = "Get student registration activity")
    public Response<PageDTO<StudentActivityDTO>> getUserRegisterActivity(@PathVariable("id") Long activityId, @ModelAttribute UserActivityRequest request){
        return activityService.getUserRegisterActivity(activityId, request);
    }

    @GetMapping("{id}")
    @Operation(summary = "Find activity by id")
    public Response<ActivityDTO> findById(@PathVariable(name = "id") Long activityId){
        return activityService.findById(activityId);
    }

    @Operation(summary = "Delete Student Activity")
    @DeleteMapping("students/{id}")
    public Response<NoContentDTO> deleteUserActivity(@PathVariable("id") Long userActivityId){
        return activityService.deleteUserActivity(userActivityId);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete Activity")
    public Response<NoContentDTO> deleteActivity(@PathVariable("id") Long activityId){
        return activityService.deleteActivity(activityId);
    }

    @Operation(summary = "Get my activity")
    @GetMapping("my-activity")
    public Response<PageDTO<ActivityDTO>> myActivity(@ModelAttribute MyActivityRequest request){
        return activityService.myActivity(request);
    }

    @Operation(summary = "Cancel my activity")
    @DeleteMapping("cancel/{id}")
    public Response<NoContentDTO> cancelActivity(@PathVariable("id") Long activityId){
        return activityService.cancelActivity(activityId);
    }

}
