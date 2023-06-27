package com.capstoneproject.server.kafka.service.impl;

import com.capstoneproject.server.common.enums.ClearProofStatus;
import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.common.enums.UserActivityStatus;
import com.capstoneproject.server.domain.entity.ClearProofEntity;
import com.capstoneproject.server.domain.prefetch.PrefetchEntityProvider;
import com.capstoneproject.server.domain.repository.ClearProofRepository;
import com.capstoneproject.server.domain.repository.UserActivityRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.kafka.message.SendActivityClearProofMessage;
import com.capstoneproject.server.kafka.message.SendClearProofMessage;
import com.capstoneproject.server.kafka.service.ClearProofKafkaService;
import com.capstoneproject.server.payload.response.OnlyIDDTO;
import com.capstoneproject.server.payload.response.Response;
import com.capstoneproject.server.util.CloudinaryUtils;
import com.capstoneproject.server.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author dai.le-anh
 * @since 6/27/2023
 */

@Service
@RequiredArgsConstructor
public class ClearProofKafkaServiceImpl implements ClearProofKafkaService {
    private final UserActivityRepository userActivityRepository;
    private final CloudinaryUtils cloudinaryUtils;
    private final PrefetchEntityProvider prefetchEntityProvider;
    private final ClearProofRepository clearProofRepository;
    private final UserRepository userRepository;

    @Override
    public void sendActivityClearProof(SendActivityClearProofMessage message) {
        var userActivity = userActivityRepository.findById(message.getUserActivityId()).orElseThrow(() ->
                new ObjectNotFoundException("userActivityId", message.getUserActivityId()));

        ClearProofEntity clearProof = new ClearProofEntity();

        clearProof.setName(message.getName());
        clearProof.setDescription(cloudinaryUtils.resolveText(message.getDescription()));
        clearProof.setUserActivityId(userActivity.getUserActivityId());
        clearProof.setStatus(prefetchEntityProvider.getClearProofStatusEntityCodeMap().get(ClearProofStatus.SEND_PROOF.status));
        clearProof.setUser(userActivity.getUser());
        clearProofRepository.save(clearProof);

        userActivity.setStatus(prefetchEntityProvider.getUserActivityStatusEntityCodeMap().get(UserActivityStatus.SEND_PROOF.status));
        userActivityRepository.save(userActivity);
    }

    @Override
    public void sendClearProof(SendClearProofMessage message) {
        ClearProofEntity clearProof = new ClearProofEntity();
        var user = userRepository.findById(message.getUserId()).orElseThrow(() ->
                new ObjectNotFoundException("userId", message.getUserId()));

        clearProof.setName(message.getName());
        clearProof.setDescription(cloudinaryUtils.resolveText(message.getDescription()));
        clearProof.setActivityCategoryId(message.getSubCategoryId());
        clearProof.setUser(user);
        try {
            clearProof.setStartDate(DateTimeUtils.string2Timestamp(message.getStartDate()));
            clearProof.setEndDate(DateTimeUtils.string2Timestamp(message.getEndDate()));
        } catch (Exception e){
            e.printStackTrace();
        }

        clearProof.setStatus(prefetchEntityProvider.getClearProofStatusEntityCodeMap().get(ClearProofStatus.SEND_PROOF.status));

        clearProofRepository.save(clearProof);

    }
}
