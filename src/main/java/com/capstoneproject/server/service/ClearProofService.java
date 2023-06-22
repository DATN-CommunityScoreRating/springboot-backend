package com.capstoneproject.server.service;

import com.capstoneproject.server.payload.request.SendActivityRequest;
import com.capstoneproject.server.payload.request.clearProof.ClearProofRequest;
import com.capstoneproject.server.payload.request.clearProof.ConfirmClearProofRequest;
import com.capstoneproject.server.payload.request.clearProof.ListClearProofRequest;
import com.capstoneproject.server.payload.response.*;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

public interface ClearProofService {
    Response<OnlyIDDTO> sendActivityClearProof(SendActivityRequest request);

    Response<OnlyIDDTO> confirmActivityClearProof(Long clearProofId);

    Response<OnlyIDDTO> cancelClearProofActivity(Long clearProofId);

    Response<OnlyIDDTO> sendClearProof(ClearProofRequest request);

    Response<PageDTO<StudentClearProofDTO>> findAllClearProof(ListClearProofRequest request);

    Response<StudentClearProofDTO> findClearProofById(Long clearProofId);

    Response<OnlyIDDTO> confirmClearProof(Long clearProofId, ConfirmClearProofRequest request);

    Response<OnlyIDDTO> cancelClearProof(Long clearProofId);

    Response<ActivityClearProofDTO> getActivityClearProof(Long clearProofId);
}
