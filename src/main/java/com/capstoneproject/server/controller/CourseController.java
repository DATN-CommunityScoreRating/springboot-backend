package com.capstoneproject.server.controller;

import com.capstoneproject.server.payload.response.CourseDTO;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.service.CourseService;
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
@Tag(name = "Course APIs")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/courses")
    @Operation(summary = "List courses")
    public Response<ListDTO<CourseDTO>> getAllCourse(){
        return courseService.findAll();
    }
}
