package com.capstoneproject.server.controller;

import com.capstoneproject.server.common.constants.CommunityBKDNPermission;
import com.capstoneproject.server.payload.request.SendActivityRequest;
import com.capstoneproject.server.payload.request.clearProof.ClearProofRequest;
import com.capstoneproject.server.payload.request.clearProof.ConfirmClearProofRequest;
import com.capstoneproject.server.payload.request.clearProof.ListClearProofRequest;
import com.capstoneproject.server.payload.response.*;
import com.capstoneproject.server.service.ClearProofService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "Clear Proof APIs")
@RequestMapping("clear-proofs")
public class ClearProofController {
    private final ClearProofService clearProofService;

    @PostMapping("activities")
    @Secured(value = {
            CommunityBKDNPermission.Role.STUDENT
    })
    @Operation(summary = "Student send activity clear proof")
    public Response<OnlyIDDTO> sendActivityClearProof(@RequestBody SendActivityRequest request){
        return clearProofService.sendActivityClearProof(request);
    }

    @GetMapping("activities/{id}")
    public Response<ActivityClearProofDTO> getActivityClearProof(@PathVariable("id") Long userActivityId){
        return clearProofService.getActivityClearProof(userActivityId);
    }

    @PostMapping("activities/{id}")
    public Response<OnlyIDDTO> confirmActivityClearProof(@PathVariable(value = "id") Long clearProofId){
        return clearProofService.confirmActivityClearProof(clearProofId);
    }

    @PostMapping("activities/cancel/{id}")
    public Response<OnlyIDDTO> cancelClearProofActivity(@PathVariable(value = "id") Long clearProofId){
        return clearProofService.cancelClearProofActivity(clearProofId);
    }

    @PostMapping
    public Response<OnlyIDDTO> sendClearProof(@RequestBody ClearProofRequest request){
        return clearProofService.sendClearProof(request);
    }

    @GetMapping
    public Response<PageDTO<StudentClearProofDTO>> findAllClearProof(@ModelAttribute ListClearProofRequest request){
        return clearProofService.findAllClearProof(request);
    }

    @GetMapping("{id}")
    public Response<StudentClearProofDTO> findClearProof(@PathVariable("id") Long clearProofId){
        return clearProofService.findClearProofById(clearProofId);
    }

    @PostMapping("{id}")
    public Response<OnlyIDDTO> confirmClearProof(@PathVariable("id") Long clearProofId, @RequestBody ConfirmClearProofRequest request){
        return clearProofService.confirmClearProof(clearProofId, request);
    }

    @PostMapping("cancel/{id}")
    public Response<OnlyIDDTO> cancelClearProof(@PathVariable("id") Long clearProofId){
        return clearProofService.cancelClearProof(clearProofId);
    }

}
