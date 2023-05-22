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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDTO<T> implements Serializable {
    private Integer start;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private List<T> items;
    private Map<Object, Object> metadata;
    private String debugMessage;
}