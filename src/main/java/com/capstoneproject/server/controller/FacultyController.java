package com.capstoneproject.server.controller;

import com.capstoneproject.server.payload.response.FacultyDTO;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Faculty APIs")
public class FacultyController {
    private final FacultyService facultyService;

    @GetMapping("/faculties")
    @Operation(summary = "List faculties")
    public Response<ListDTO<FacultyDTO>> getAllFaculties(){
        return facultyService.findAll();
    }
}
