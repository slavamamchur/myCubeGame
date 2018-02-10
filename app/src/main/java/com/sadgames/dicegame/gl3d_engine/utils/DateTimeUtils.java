package com.sadgames.dicegame.gl3d_engine.utils;


import java.text.DateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }
}
