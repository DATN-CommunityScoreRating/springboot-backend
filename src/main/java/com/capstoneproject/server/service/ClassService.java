package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.request.AddClassRequest;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.payload.response.*;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

public interface ClassService {
    Response<PageDTO<ClassDTO>> findClass(GetAllClassRequest request);

    Response<OnlyIDDTO> addClass(AddClassRequest request);

    Response<ClassDTO> getClassById(Long classId);

    Response<OnlyIDDTO> updateClass(Long classId, AddClassRequest request);

    Response<NoContentDTO> deleteClass(Long classId);
}
