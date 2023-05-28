package com.capstoneproject.server.domain.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadStudentCSV {
    @CsvBindByPosition(position = 0)
    private String className;
    @CsvBindByPosition(position = 1)
    private String studentId;
    @CsvBindByPosition(position = 2)
    private String username;
    @CsvBindByPosition(position = 3)
    private String password;
    @CsvBindByPosition(position = 4)
    private String fullName;
    @CsvBindByPosition(position = 5)
    private String email;
    @CsvBindByPosition(position = 6)
    private String phoneNumber;

}
