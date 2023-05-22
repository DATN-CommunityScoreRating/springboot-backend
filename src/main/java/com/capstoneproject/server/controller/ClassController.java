package com.capstoneproject.server.controller;

import com.capstoneproject.server.payload.request.AddClassRequest;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.payload.response.ClassDTO;
import com.capstoneproject.server.payload.response.OnlyIDDTO;
import com.capstoneproject.server.payload.response.PageDTO;
import com.capstoneproject.server.payload.response.Response;
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
}
