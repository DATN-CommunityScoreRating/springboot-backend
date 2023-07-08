package com.capstoneproject.server.kafka.service.impl;

import com.capstoneproject.server.common.enums.UserActivityStatus;
import com.capstoneproject.server.domain.entity.UserActivityEntity;
import com.capstoneproject.server.domain.prefetch.PrefetchEntityProvider;
import com.capstoneproject.server.domain.repository.ActivityRepository;
import com.capstoneproject.server.domain.repository.UserActivityRepository;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.kafka.message.CancelUserActivityMessage;
import com.capstoneproject.server.kafka.message.RegistrationActivityMessage;
import com.capstoneproject.server.kafka.service.StudentActivityKafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author dai.le-anh
 * @since 6/26/2023
 */

@Service
@RequiredArgsConstructor
@Log4j2
public class StudentActivityKafkaServiceImpl implements StudentActivityKafkaService {
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final UserActivityRepository userActivityRepository;
    private final PrefetchEntityProvider prefetchEntityProvider;

    @Override
    public void studentRegistrationActivity(RegistrationActivityMessage registrationActivityMessage) {
        var user = userRepository.findById(registrationActivityMessage.getUserId()).orElseThrow(() ->
                new ObjectNotFoundException("userId", registrationActivityMessage.getUserId()));
        var activity = activityRepository.findById(registrationActivityMessage.getActivityId()).orElseThrow(
                () -> new ObjectNotFoundException("activityId", registrationActivityMessage.getActivityId())
        );

        UserActivityEntity userActivity = new UserActivityEntity();
        userActivity.setUser(user);
        userActivity.setActivity(activity);
        userActivity.setStatus(prefetchEntityProvider.getUserActivityStatusEntityCodeMap().get(UserActivityStatus.REGISTERED.name()));
        var userActivitySaved = userActivityRepository.save(userActivity);
        log.info("Save success user activity id {}", userActivitySaved.getUserActivityId());

    }

    @Override
    public void cancelRegistrationActivity(CancelUserActivityMessage cancelUserActivityMessage) {
        var userActivity = userActivityRepository.findByActivityIdAndUserId(cancelUserActivityMessage.getActivityId(), cancelUserActivityMessage.getUserId())
                .orElseThrow(() -> new ObjectNotFoundException("activityId", cancelUserActivityMessage.getActivityId()));

        userActivityRepository.delete(userActivity);
    }
}
