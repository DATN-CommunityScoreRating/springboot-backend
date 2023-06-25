package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.cache.JacksonRedisUtil;
import com.capstoneproject.server.common.CommunityBKDNPrincipal;
import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.domain.dto.ImportClassCsv;
import com.capstoneproject.server.domain.entity.ClassEntity;
import com.capstoneproject.server.domain.prefetch.PrefetchEntityProvider;
import com.capstoneproject.server.domain.repository.ClassRepository;
import com.capstoneproject.server.domain.repository.CourseRepository;
import com.capstoneproject.server.domain.repository.FacultyRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.domain.repository.dsl.ClassDslRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.payload.request.ImportRequest;
import com.capstoneproject.server.payload.response.UploadDTO;
import com.capstoneproject.server.payload.request.AddClassRequest;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.ClassService;
import com.capstoneproject.server.util.RequestUtils;
import com.capstoneproject.server.util.SecurityUtils;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@RequiredArgsConstructor
@Service
public class ClassServiceImpl implements ClassService {
    private final ClassDslRepository classDslRepository;
    private final PrefetchEntityProvider prefetchEntityProvider;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final SecurityUtils securityUtils;
    private final JacksonRedisUtil jacksonRedisUtil;

    @Override
    public Response<OnlyIDDTO> updateClass(Long classId, AddClassRequest request) {
        var classEntity = classRepository.findById(classId).orElseThrow(() ->
                new ObjectNotFoundException("classId", classId));

        Map<String, String> error = new HashMap<>();
        // TODO: validate
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

    @Override
    public Response<NoContentDTO> deleteClass(Long classId) {
        var clazz = classRepository.findById(classId).orElseThrow(() ->
                new ObjectNotFoundException("classId", classId));

        classRepository.delete(clazz);

        return Response.<NoContentDTO>newBuilder()
                .setSuccess(true)
                .setData(NoContentDTO.builder().build())
                .build();
    }

    @Override
    public Response<UploadDTO<UploadClassDTO>> uploadClass(MultipartFile csvFile) {
        try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))){
            CsvToBean<ImportClassCsv> csvToBean = new CsvToBeanBuilder<ImportClassCsv>(reader)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .withType(ImportClassCsv.class)
                    .build();
            List<ImportClassCsv> classCsv = csvToBean.parse();
            var listClassName = classCsv.stream().map(ImportClassCsv::getClassName).collect(Collectors.toList());
            var classExist = classRepository.findAllByClassNames(listClassName).stream()
                    .map(ClassEntity::getClassName)
                    .collect(Collectors.toList());
            List<UploadClassDTO> result = new ArrayList<>();
            classCsv.forEach(csv -> {
                var classBuilder = UploadClassDTO.builder()
                        .className(csv.getClassName())
                        .faculty(csv.getFaculty())
                        .course(csv.getCourse());
                Map<String, String> error = new HashMap<>();
                if (StringUtils.isBlank(csv.getClassName())){
                    error.put("className", ErrorCode.NOT_EMPTY.name());
                }
                if (classExist.contains(csv.getClassName())){
                    error.put("className", ErrorCode.ALREADY_EXIST.name());
                }

                if (prefetchEntityProvider.getCourseEntityCodeMap().get(csv.getCourse()) == null){
                    error.put("course", ErrorCode.INVALID_VALUE.name());
                }

                if (prefetchEntityProvider.getFacultyEntityCodeMap().get(csv.getFaculty()) == null){
                    error.put("faculty", ErrorCode.INVALID_VALUE.name());
                }
                if (!error.isEmpty()){
                    classBuilder.error(error);
                }
                result.add(classBuilder.build());
            });

            UUID uuid = UUID.randomUUID();

            jacksonRedisUtil.put(String.format("%s_%s", securityUtils.getPrincipal().getUserId(), uuid), result, JacksonRedisUtil.DEFAULT_DURATION);

            return Response.<UploadDTO<UploadClassDTO>>newBuilder()
                    .setSuccess(true)
                    .setData(UploadDTO.<UploadClassDTO>builder()
                            .totalElements((long) result.size())
                            .items(result)
                            .correlationId(uuid)
                            .build())
                    .build();

        } catch (Exception e){
            return Response.<UploadDTO<UploadClassDTO>>newBuilder()
                    .setSuccess(false)
                    .setMessage("Error Reader")
                    .setErrorCode(ErrorCode.IO_ERROR)
                    .build();
        }
    }

    @Override
    public Response<ImportDTO> importClass(ImportRequest request) {
        var classList = jacksonRedisUtil.getAsList(String.format("%s_%s",securityUtils.getPrincipal().getUserId(), request.getCorrelationId()), UploadClassDTO.class);
        var total = classList.size();
        var successList = classList.stream().filter(s -> s.getError()  == null || s.getError().isEmpty()).collect(Collectors.toList());

        List<ClassEntity> classEntities = new ArrayList<>();
        successList.forEach(clazz -> {
            ClassEntity classEntity = new ClassEntity();
            classEntity.setFaculty(prefetchEntityProvider.getFacultyEntityCodeMap().get(clazz.getFaculty()));
            classEntity.setCourseEntity(prefetchEntityProvider.getCourseEntityCodeMap().get(clazz.getCourse()));
            classEntity.setClassName(clazz.getClassName());
            classEntities.add(classEntity);
        });

        if (!classEntities.isEmpty()){
            classRepository.saveAll(classEntities);
        }

        return Response.<ImportDTO>newBuilder()
                .setSuccess(true)
                .setData(ImportDTO.builder()
                        .total(total)
                        .totalSuccess(successList.size())
                        .totalError(total - successList.size())
                        .build())
                .build();
    }

    @Override
    public Response<PageDTO<ClassDTO>> findClass(GetAllClassRequest request) {
        var principal = securityUtils.getPrincipal();
        var user = userRepository.findById(principal.getUserId()).get();
        var classPage = classDslRepository.getListClass(request, principal, user.getFacultyId());
        return Response.<PageDTO<ClassDTO>>newBuilder()
                .setSuccess(true)
                .setData(PageDTO.<ClassDTO>builder()
                        .size(request.getSize())
                        .page(request.getPage())
                        .totalElements(classPage.getTotal())
                        .totalPages(RequestUtils.getTotalPage(classPage.getTotal(), request))
                        .items(classPage.getItems().stream()
                                .map(this::classMap)
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

    @Override
    public Response<ClassDTO> getClassById(Long classId) {
        var clazz = classRepository.findByIdAndFetchFacultyAndCourse(classId).orElseThrow(() ->
                new ObjectNotFoundException("classId", classId));

        return Response.<ClassDTO>newBuilder()
                .setSuccess(true)
                .setData(classMap(clazz))
                .build();
    }

    private void validateAddClass(Map<String, String> error) {
//        TODO: validate add class
    }

    private ClassDTO classMap(ClassEntity clazz){
        return ClassDTO.builder()
                .classId(clazz.getClassId())
                .courseId(clazz.getCourseEntity().getCourseId())
                .courseName(clazz.getCourseEntity().getName())
                .facultyName(clazz.getFaculty().getFacultyName())
                .facultyId(clazz.getFaculty().getFacultyId())
                .className(clazz.getClassName())
                .build();
    }
}
