package com.example.adam.nfcalarm.model;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.util.Views;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 16-01-18.
 */
public class AlarmData {
    public static final String EXTRA_HOUR = "com.example.adam.nfcalarm.model.HOUR";
    public static final String EXTRA_MINUTE = "com.example.adam.nfcalarm.model.MINUTE";

    private SharedPreferences sharedPreferences;
    private JSONArray alarms;
    private Activity appActivity;

    public AlarmData(Activity activity) {
        appActivity = activity;
        sharedPreferences = activity.getSharedPreferences(ApplicationActivity.NAME, Context.MODE_PRIVATE);
        //// TODO: 16-01-27 remove instantiation of AlarmData and proceeding readPreferences
        readPreferences();
    }

    public void setAlarms(JSONArray json) {
        sharedPreferences.edit().putString(ApplicationActivity.ALARM_KEY, json.toString()).apply();
        List<AlarmModel> currentList = toList();

        // enable / disable WakefulAlarmReceiver via ApplicationActivity
        boolean scheduledAlarm = false;
        for (AlarmModel currentModel : currentList) {
            if (currentModel.isActive) {
                scheduledAlarm = true;
                break;
            }
        }
        ((ApplicationActivity)appActivity).doAlarmSchedule(scheduledAlarm);
    }

    public JSONArray toJSONArray() {
        readPreferences();
        return alarms;
    }

    public List<AlarmModel> toList() {
        List<AlarmModel> models = new ArrayList<>();
        readPreferences();

        int alarmsLength = alarms.length();
        for (int i = 0; i < alarmsLength; i++) {
            models.add(new AlarmModel(alarms.optString(i)));
        }

        return models;
    }

    private void readPreferences() {
        String alarmsAsString = sharedPreferences.getString(ApplicationActivity.ALARM_KEY, "");

        try {
            alarms = new JSONArray(alarmsAsString);
        } catch (JSONException e) {
            e.printStackTrace();
            alarms = new JSONArray();
        }
    }
}