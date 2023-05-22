package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.domain.entity.ClassEntity;
import com.capstoneproject.server.domain.repository.ClassRepository;
import com.capstoneproject.server.domain.repository.CourseRepository;
import com.capstoneproject.server.domain.repository.FacultyRepository;
import com.capstoneproject.server.domain.repository.dsl.ClassDslRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.payload.request.AddClassRequest;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.ClassService;
import com.capstoneproject.server.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@RequiredArgsConstructor
@Service
public class ClassServiceImpl implements ClassService {
    private final ClassDslRepository classDslRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final ClassRepository classRepository;
    @Override
    public Response<PageDTO<ClassDTO>> findClass(GetAllClassRequest request) {
        var classPage = classDslRepository.getListClass(request);
        return Response.<PageDTO<ClassDTO>>newBuilder()
                .setSuccess(true)
                .setData(PageDTO.<ClassDTO>builder()
                        .size(request.getSize())
                        .page(request.getPage())
                        .totalElements(classPage.getTotal())
                        .totalPages(RequestUtils.getTotalPage(classPage.getTotal(), request))
                        .items(classPage.getItems().stream()
                                .map(c -> ClassDTO.builder()
                                        .classId(c.getClassId())
                                        .courseId(c.getCourseEntity().getCourseId())
                                        .courseName(c.getCourseEntity().getName())
                                        .facultyName(c.getFaculty().getFacultyName())
                                        .facultyId(c.getFaculty().getFacultyId())
                                        .className(c.getClassName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Override
    public Response<OnlyIDDTO> addClass(AddClassRequest request) {
        ClassEntity classEntity = new ClassEntity();

        Map<String, String> error = new HashMap<>();

        validateAddClass(error);
        var faculty = facultyRepository.findById(request.getFacultyId()).orElseThrow(() ->
                new ObjectNotFoundException("facultyId", request.getFacultyId()));

        var course = courseRepository.findById(request.getCourseId()).orElseThrow(() ->
                new ObjectNotFoundException("courseId", request.getCourseId()));

        classEntity.setClassName(request.getClassName());
        classEntity.setCourseEntity(course);
        classEntity.setFaculty(faculty);
        var classSaved = classRepository.save(classEntity);



        return Response.<OnlyIDDTO>newBuilder()
                .setSuccess(true)
                .setData(OnlyIDDTO.builder()
                        .id(classSaved.getClassId())
                        .build())
                .build();
    }

    private void validateAddClass(Map<String, String> error) {
//        TODO: validate add class
    }
}
