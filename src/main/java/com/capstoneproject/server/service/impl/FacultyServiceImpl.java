package com.capstoneproject.server.service.impl;

import com.capstoneproject.server.domain.repository.FacultyRepository;
import com.capstoneproject.server.payload.response.ListDTO;
import com.capstoneproject.server.payload.response.FacultyDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;
    @Override
    public Response<ListDTO<FacultyDTO>> findAll() {
        var faculties = facultyRepository.findAll();
        return Response.<ListDTO<FacultyDTO>>newBuilder()
                .setSuccess(true)
                .setData(ListDTO.<FacultyDTO>                builder()
                        .totalElements((long)faculties.size())
                        .items(faculties.stream()
                                .map(f -> FacultyDTO.builder()
                                        .facultyId(f.getFacultyId())
                                        .facultyName(f.getFacultyName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();

    }
}
