package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.CourseDTO;
import com.capstoneproject.server.payload.response.Response;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

public interface CourseService {
    Response<ListDTO<CourseDTO>> findAll();
}
