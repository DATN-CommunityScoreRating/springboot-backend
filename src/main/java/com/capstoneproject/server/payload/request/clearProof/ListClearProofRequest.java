package com.capstoneproject.server.payload.request.clearProof;

import com.capstoneproject.server.payload.request.AbstractPageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 6/20/2023
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListClearProofRequest extends AbstractPageRequest {
    private Long facultyId;
    private Long categoryId;
}
