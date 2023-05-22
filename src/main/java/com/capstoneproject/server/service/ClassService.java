package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.request.AddClassRequest;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.payload.response.ClassDTO;
import com.capstoneproject.server.payload.response.OnlyIDDTO;
import com.capstoneproject.server.payload.response.PageDTO;
import com.capstoneproject.server.payload.response.Response;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

public interface ClassService {
    Response<PageDTO<ClassDTO>> findClass(GetAllClassRequest request);

    Response<OnlyIDDTO> addClass(AddClassRequest request);
}
