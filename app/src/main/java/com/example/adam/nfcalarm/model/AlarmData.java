package com.example.adam.nfcalarm.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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

    private SharedPreferences sharedPreferences;
    private JSONArray alarms;

    public AlarmData(Activity activity) {
        if (!Views.isActivityNull(activity)) {
            sharedPreferences = activity.getSharedPreferences(ApplicationActivity.NAME, Context.MODE_PRIVATE);
        }
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

    public void setAlarms(JSONArray json) {
        sharedPreferences.edit().putString(ApplicationActivity.ALARM_KEY, json.toString()).apply();
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
