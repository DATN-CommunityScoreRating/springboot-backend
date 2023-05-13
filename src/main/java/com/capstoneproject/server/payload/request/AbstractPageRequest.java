package com.capstoneproject.server.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * @author lkadai0801
 * @since 12/05/2023
 */
@Getter
@Setter
public abstract class AbstractPageRequest {
    @Schema(minimum = "1", nullable = true, description = "Requested page. Start from 1. Default 1 if not given")
    @Min(1)
    private Integer page = 1;

    @Schema(minimum = "1", nullable = true, description = "Requested size. Start from 1. Default 100 if not given")
    @Min(1)
    private Integer size = 100;

    @Schema(nullable = true, description = "Sort property. Omitted if not given")
    private String sort;

    @Schema(nullable = true, description = "Sort direction. Accepted values - asc,desc. Default to desc if not given")
    private String direction;
}
