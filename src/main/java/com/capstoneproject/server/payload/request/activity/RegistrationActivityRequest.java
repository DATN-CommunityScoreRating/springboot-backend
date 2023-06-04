package com.capstoneproject.server.payload.request.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author dai.le-anh
 * @since 6/4/2023
 */

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RegistrationActivityRequest {
    @NotNull
    private Long activityId;
}
