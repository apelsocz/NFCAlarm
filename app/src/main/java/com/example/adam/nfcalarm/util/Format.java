package com.example.adam.nfcalarm.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Format {
    public static String dayOfYear(int dayOfWeek) {
        String day = "";

        if (dayOfWeek == Calendar.SUNDAY) {
            day = "sunday";
        }
        else if (dayOfWeek == Calendar.MONDAY) {
            day = "monday";
        }
        else if (dayOfWeek == Calendar.TUESDAY) {
            day = "tuesday";
        }
        else if (dayOfWeek == Calendar.WEDNESDAY) {
            day = "wednesday";
        }
        else if (dayOfWeek == Calendar.THURSDAY) {
            day = "thursday";
        }
        else if (dayOfWeek == Calendar.FRIDAY) {
            day = "friday";
        }
        else if (dayOfWeek == Calendar.SATURDAY) {
            day = "saturday";
        }
        return day;
    }

    public static String formatDate(long millis) {
        final DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        Date date = new Date(millis);
        return df.format(date);
    }

    public static String formatTime(long millis) {
        final DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);
        Date date = new Date(millis);
        return tf.format(date);
    }
}