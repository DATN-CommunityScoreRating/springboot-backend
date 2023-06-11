package com.capstoneproject.server.payload.response.activity;

import com.capstoneproject.server.payload.response.UserDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author dai.le-anh
 * @since 6/11/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StudentActivityDTO extends UserDTO {
    private Long userActivityId;
    private Long statusId;
    private String status;
}
