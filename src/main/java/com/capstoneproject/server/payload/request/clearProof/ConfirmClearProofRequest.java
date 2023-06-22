package com.capstoneproject.server.payload.request.clearProof;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 6/20/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmClearProofRequest {
    private Integer score;
}
