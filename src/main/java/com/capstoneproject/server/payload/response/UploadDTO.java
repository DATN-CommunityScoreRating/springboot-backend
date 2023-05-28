package com.capstoneproject.server.payload.response;

import com.capstoneproject.server.payload.response.ListDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@Getter
@Setter
@SuperBuilder
public class UploadDTO<T> extends ListDTO<T> {
    private UUID correlationId;
}