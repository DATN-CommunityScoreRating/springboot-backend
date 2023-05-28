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
public class ImportClassCsv {
    @CsvBindByPosition(position = 0)
    private String course;

    @CsvBindByPosition(position = 1)
    private String faculty;

    @CsvBindByPosition(position = 2)
    private String className;

    public static ImportClassCsv headers() {
        var header = new ImportClassCsv();
        header.setCourse("Course (Required)");
        header.setFaculty("Faculty (Required)");
        header.setClassName("Class name (Required)");

        return header;
    }

}
