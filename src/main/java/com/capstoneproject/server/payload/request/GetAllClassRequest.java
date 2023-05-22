package com.capstoneproject.server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllClassRequest extends AbstractPageRequest{
    private Long facultyId;
    private Long courseId;
    private String searchTerm;
}
