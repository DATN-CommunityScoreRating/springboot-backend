package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.FacultyDTO;
import com.capstoneproject.server.payload.response.Response;

public interface FacultyService {
    Response<ListDTO<FacultyDTO>> findAll();
}
