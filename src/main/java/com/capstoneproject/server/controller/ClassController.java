package com.capstoneproject.server.controller;

import com.capstoneproject.server.payload.request.AddClassRequest;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("classes")
public class ClassController {
    private final ClassService classService;

    @GetMapping
    public Response<PageDTO<ClassDTO>> getAllClass(@ModelAttribute("request")GetAllClassRequest request){
        return classService.findClass(request);
    }

    @PostMapping
    public Response<OnlyIDDTO> addClass(@RequestBody @Valid AddClassRequest request){
        return classService.addClass(request);
    }

    @GetMapping("{id}")
    public Response<ClassDTO> getClassById(@PathVariable("id") Long classId){
        return classService.getClassById(classId);
    }

    @PutMapping("{id}")
    public Response<OnlyIDDTO> updateClass(@PathVariable("id") Long classId, AddClassRequest request){
        return classService.updateClass(classId, request);
    }

    @DeleteMapping("{id}")
    public Response<NoContentDTO> deleteClass(@PathVariable("id") Long classId){
        return classService.deleteClass(classId);
    }
}
