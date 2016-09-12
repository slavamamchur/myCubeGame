package com.cubegames.slava.cubegame;


import java.text.DateFormat;
import java.util.Date;

public class Utils {
    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }
}
