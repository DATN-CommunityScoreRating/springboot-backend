package com.capstoneproject.server.kafka.message;

import lombok.*;

/**
 * @author dai.le-anh
 * @since 6/27/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(setterPrefix = "set", builderMethodName = "newBuilder")
public class SendClearProofMessage {
    private Long userId;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private Long subCategoryId;
}
