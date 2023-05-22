package com.capstoneproject.server.controller;

import com.capstoneproject.server.payload.response.FacultyDTO;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@RestController
@RequiredArgsConstructor
public class FacultyController {
    private final FacultyService facultyService;

    @GetMapping("/faculties")
    public Response<ListDTO<FacultyDTO>> getAllFaculties(){
        return facultyService.findAll();
    }
}
