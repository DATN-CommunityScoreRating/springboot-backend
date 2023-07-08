package com.capstoneproject.server.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public static Date move2EndTimeOfDay(Date input) {
        Date res = null;
        if (input != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(input);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 99);
            res = cal.getTime();
        }
        return res;
    }

    public static Date move2BeginTimeOfDay(Date input) {
        Date res = null;
        if (input != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(input);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            res = cal.getTime();
        }
        return res;
    }
}