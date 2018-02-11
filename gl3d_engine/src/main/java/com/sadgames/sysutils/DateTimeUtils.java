package com.sadgames.sysutils;


import java.text.DateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }
}
