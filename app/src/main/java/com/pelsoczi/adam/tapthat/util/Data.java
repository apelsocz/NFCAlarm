package com.pelsoczi.adam.tapthat.util;

import com.pelsoczi.adam.tapthat.model.AlarmModel;
import com.pelsoczi.data.Alarm;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Data {
    public static JSONArray modelsAsJSON(String modelsAsString) {
        JSONArray modelsAsJSON;
        try {
            modelsAsJSON = new JSONArray(modelsAsString);
        } catch (JSONException e) {
            e.printStackTrace();
            modelsAsJSON = new JSONArray();
        }
        return modelsAsJSON;
    }

    public static List<AlarmModel> modelsAsList (JSONArray json) {
        List<AlarmModel> modelsAsList = new ArrayList<>();

        int length = json.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                modelsAsList.add(new AlarmModel(json.optString(i)));
            }
        }
        return modelsAsList;
    }

    public static boolean activeModelInList(List<AlarmModel> list) {
        boolean found = false;
        for (AlarmModel model : list) {
            if (model.isActive) {
                found = true;
                break;
            }
        }
//        Log.d("containsActiveAlarm", String.valueOf(found));
        return found;
    }

    public static boolean isDayActive(int dayOfWeek, Alarm model) {
        boolean active = false;

        if (dayOfWeek == Calendar.SUNDAY) {
            active = model.getSunday();
        }
        else if (dayOfWeek == Calendar.MONDAY) {
            active = model.getMonday();
        }
        else if (dayOfWeek == Calendar.TUESDAY) {
            active = model.getTuesday();
        }
        else if (dayOfWeek == Calendar.WEDNESDAY) {
            active = model.getWednesday();
        }
        else if (dayOfWeek == Calendar.THURSDAY) {
            active = model.getThursday();
        }
        else if (dayOfWeek == Calendar.FRIDAY) {
            active = model.getFriday();
        }
        else if (dayOfWeek == Calendar.SATURDAY) {
            active = model.getSaturday();
        }
        return active;
    }
}