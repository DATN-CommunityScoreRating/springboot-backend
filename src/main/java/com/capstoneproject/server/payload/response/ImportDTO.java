package com.capstoneproject.server.payload.response;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ImportDTO {
    private Integer total;
    private Integer totalSuccess;
    private Integer totalError;
}
