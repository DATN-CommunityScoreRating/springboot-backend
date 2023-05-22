package com.capstoneproject.server.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Getter
@Setter
@SuperBuilder
public class ListDTO<T> implements Serializable {
    private Long totalElements;
    private List<T> items;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Object, Object> metadata;
}
