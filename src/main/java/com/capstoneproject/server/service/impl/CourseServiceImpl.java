package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.domain.repository.CourseRepository;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.CourseDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;


    @Override
    public Response<ListDTO<CourseDTO>> findAll() {
        var courses = courseRepository.findAll();
        return Response.<ListDTO<CourseDTO>>newBuilder()
                .setSuccess(true)
                .setData(ListDTO.<CourseDTO>builder()
                        .totalElements((long) courses.size())
                        .items(courses.stream()
                                .map(c -> CourseDTO.builder()
                                        .courseId(c.getCourseId())
                                        .courseName(c.getName())
                                        .code(c.getCode())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }
}
