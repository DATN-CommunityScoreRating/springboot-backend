package com.capstoneproject.server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImportRequest {
    @NotNull
    private UUID correlationId;
}
