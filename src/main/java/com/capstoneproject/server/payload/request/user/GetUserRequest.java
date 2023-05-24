package com.capstoneproject.server.payload.request.user;

import com.capstoneproject.server.payload.request.AbstractPageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author dai.le-anh
 * @since 5/23/2023
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserRequest extends AbstractPageRequest {
    private Long roleId;
    private String searchTerm;
    private Long classId;
    private Long facultyId;
}
