package com.example.adam.nfcalarm.util;

import java.util.Calendar;

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
}