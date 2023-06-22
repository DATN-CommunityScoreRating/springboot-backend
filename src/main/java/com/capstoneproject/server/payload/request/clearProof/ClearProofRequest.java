package com.capstoneproject.server.payload.request.clearProof;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClearProofRequest {
    private String name;
    private String startDate;
    private String endDate;
    private Long subCategoryId;
    private String description;
}
