package com.capstoneproject.server.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

public class DateTimeUtils {
    private static final String pattern = "dd-MM-yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public static Timestamp string2Timestamp(String date) throws ParseException {
        return new Timestamp(simpleDateFormat.parse(date).getTime());
    }

    public static String timestamp2String(Timestamp timestamp){
        return simpleDateFormat.format(timestamp);
    }
}