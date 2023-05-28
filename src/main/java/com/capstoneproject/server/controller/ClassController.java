package com.capstoneproject.server.controller;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.payload.request.ImportRequest;
import com.capstoneproject.server.payload.response.UploadDTO;
import com.capstoneproject.server.payload.request.AddClassRequest;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@RequiredArgsConstructor
@RestController
@Tag(name = "Class APIs")
@RequestMapping("classes")
public class ClassController {
    private final ClassService classService;

    @GetMapping
    @Operation(summary = "List class")
    @Parameters(value = {
            @Parameter(in = QUERY, name = "page", description = "Requested page. Start from 1. Default to 1 if not given", example = "1", schema = @Schema(minimum = "1")),
            @Parameter(in = QUERY, name = "size", description = "Requested page size. Default to 100 if not given", example = "100", schema = @Schema(minimum = "1")),
            @Parameter(in = QUERY, name = "sort", description = "Sort property. Omitted if not given", example = "lastName",
                    schema = @Schema(allowableValues = {"course", "faculty", "className"})),
            @Parameter(in = QUERY, name = "direction", description = "Sort direction. Accepted values - asc,desc. Omitted if sort not given. Default to asc",
                    example = "asc", schema = @Schema(allowableValues = {"asc", "desc"})),
            @Parameter(in = QUERY, name = "facultyId", description = "Filter with faculty. Not apply if not given", example = "1"),
            @Parameter(in = QUERY, name = "courseId", description = "Filter with course. Not apply if not given", example = "1"),
            @Parameter(in = QUERY, name = "searchTerm", description = "Search with keyword, not apply if not given", example = "Nhat")
    })
    public Response<PageDTO<ClassDTO>> getAllClass(@ModelAttribute("request")GetAllClassRequest request){
        return classService.findClass(request);
    }

    @PostMapping
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    @Operation(summary = "Add Class")
    public Response<OnlyIDDTO> addClass(@RequestBody @Valid AddClassRequest request){
        return classService.addClass(request);
    }

    @GetMapping("{id}")
    @Operation(summary = "GEt class By Id")
    public Response<ClassDTO> getClassById(@PathVariable("id") Long classId){
        return classService.getClassById(classId);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update class")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    public Response<OnlyIDDTO> updateClass(@PathVariable("id") Long classId, AddClassRequest request){
        return classService.updateClass(classId, request);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete class")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    public Response<NoContentDTO> deleteClass(@PathVariable("id") Long classId){
        return classService.deleteClass(classId);
    }

    @PostMapping("upload")
    @Parameter(in = ParameterIn.QUERY, name = "file", description = "File csv import class")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    @Operation(summary = "Upload class")
    public Response<UploadDTO<UploadClassDTO>> uploadClass(@RequestParam("file")MultipartFile csvFile){
        return classService.uploadClass(csvFile);
    }

    @PostMapping("import")
    @Operation(summary = "Import class")
    @Secured(value = {
            CommunityBKDNPermission.Role.ADMIN
    })
    public Response<ImportDTO> importClass(@RequestBody ImportRequest request){
        return classService.importClass(request);
    }
}
