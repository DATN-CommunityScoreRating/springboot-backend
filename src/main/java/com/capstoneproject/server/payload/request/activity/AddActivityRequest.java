package com.capstoneproject.server.payload.request.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddActivityRequest {
    @NotNull
    private String activityName;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
    @NotNull
    private String startRegister;
    @NotNull
    private String endRegister;
    @NotNull
    private String location;
    @NotNull
    private String description;
    @NotNull
    private Integer maxQuantity;
    @NotNull
    private Integer score;
}
