package com.sadgames.sysutils.common;


import java.text.DateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }
}
