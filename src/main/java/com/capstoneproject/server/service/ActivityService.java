package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.request.activity.AddActivityRequest;
import com.capstoneproject.server.payload.request.activity.ListActivitiesRequest;
import com.capstoneproject.server.payload.request.activity.RegistrationActivityRequest;
import com.capstoneproject.server.payload.request.activity.UserActivityRequest;
import com.capstoneproject.server.payload.response.OnlyIDDTO;
import com.capstoneproject.server.payload.response.PageDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.payload.response.UserDTO;
import com.capstoneproject.server.payload.response.activity.ActivityDTO;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

public interface ActivityService {
    Response<OnlyIDDTO> addActivity(AddActivityRequest request);

    Response<PageDTO<ActivityDTO>> listActivity(ListActivitiesRequest request);

    Response<OnlyIDDTO> registrationActivity(RegistrationActivityRequest request);

    Response<PageDTO<UserDTO>> getUserRegisterActivity(Long activityId, UserActivityRequest request);

    Response<ActivityDTO> findById(Long activityId);
}
