/*
package com.example.adam.nfcalarm.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.util.Views;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmData {
    public static final String NEXT_KEY = "com.example.adam.nfcalarm.model.NEXT_KEY";
    public static final String ALARMS_KEY = "com.example.adam.nfcalarm.model.ALARMS_KEY";
    public static final String ALARM_SET = "com.example.adam.nfcalarm.model.SET_ALARM";
    public static final String ALARM_CANCEL = "com.example.adam.nfcalarm.model.CANCEL_ALARM";
    private static final String ALARM_SNOOZE = "com.example.adam.nfcalarm.model.SNOOZE_ALARM";
    public static final long NO_ALARMS = 0;

    private SharedPreferences sharedPreferences;
    private JSONArray alarms;
    private Activity appActivity;
    private long nextMillis;

    public AlarmData(Context context) {
        // // TODO: 16-02-19 consider using AlarmData.NAME as sharedpereferences 'name'
        sharedPreferences = context.getSharedPreferences(ApplicationActivity.NAME, Context.MODE_PRIVATE);
        //// TODO: 16-01-27 remove instantiation of AlarmData and proceeding readPreferences
        Log.d("Launched", "AlarmData() readAlarms()");
        readAlarms();
        nextMillis = readNextMillis();
    }

    public void setApplicationActivity(Activity activity) {
        appActivity = activity;
    }

    public long setNextAlarm(String active) {
        if (active.equals(ALARM_SET)) {
            long next = getNextAlarmInMillis();
            writeNextMillis(next);
            return next;
        }
        else if (active.equals(ALARM_SNOOZE)) {
            
        }
        else if (active.equals(ALARM_CANCEL)) {

        }
        return NO_ALARMS;
    }

    public void setAlarms(JSONArray json) {
        writeAlarms(json);
        List<AlarmModel> alarmList = toList();

        // enable / disable WakefulAlarmReceiver via ApplicationActivity
        boolean scheduledAlarm = false;

        for (AlarmModel alarmModel : alarmList) {
            if (alarmModel.isActive) {
                scheduledAlarm = true;
                break;
            }
        }

        if (!Views.isActivityNull(appActivity)) {
            ((ApplicationActivity)appActivity).doScheduling(scheduledAlarm);
        }
    }

    public JSONArray toJSONArray() {
        Log.d("Launched", "toJSONArray() readAlarms()");
        readAlarms();
        return alarms;
    }

    public List<AlarmModel> toList() {
        List<AlarmModel> models = new ArrayList<>();
        Log.d("Launched", "toList() readAlarms()");
        readAlarms();

        int alarmsLength = alarms.length();
        for (int i = 0; i < alarmsLength; i++) {
            models.add(new AlarmModel(alarms.optString(i)));
        }

        return models;
    }

    private void readAlarms() {
        String alarmsAsString = sharedPreferences.getString(ApplicationActivity.ALARM_KEY, "");

        Log.d("Launched", "reading...");

        try {
            alarms = new JSONArray(alarmsAsString);
        } catch (JSONException e) {
            e.printStackTrace();
            alarms = new JSONArray();
        }
    }

    private void writeAlarms(JSONArray json) {
//        sharedPreferences.edit().putString(ApplicationActivity.ALARM_KEY, json.toString()).apply();
        sharedPreferences.edit().putString(ALARMS_KEY, json.toString()).apply();
    }

    private long readNextMillis() {
        return sharedPreferences.getLong(AlarmData.NEXT_KEY, NO_ALARMS);
    }

    private void writeNextMillis(long millis) {
        if (millis != nextMillis) {
            sharedPreferences.edit().putLong(AlarmData.NEXT_KEY, millis).apply();
        }
    }

    public long getNextAlarmInMillisTroubleshooting() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 10);

        return calendar.getTimeInMillis();
    }

    private long getNextAlarmInMillis() {
        Log.d("getNextAlarmMillis()", "Calling method");
        long returnMillis = nextMillis;

        Calendar calNext = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calModel = Calendar.getInstance();
        calNext.setTimeInMillis(System.currentTimeMillis());
        calNow.setTimeInMillis(System.currentTimeMillis());
        calModel.setTimeInMillis(System.currentTimeMillis());
        Date dateNext;
        Date dateNow = calNow.getTime();
        Date dateModel;
        List<AlarmModel> alarmList = toList();

        //// TODO: 16-03-02 temporary setting date of Next Alarm for testing
        calNext.add(Calendar.DAY_OF_YEAR, 7);
        dateNext = calNext.getTime();

        if (returnMillis != NO_ALARMS && alarmList.size() > 0 ) {
//            calNext.setTimeInMillis(returnMillis);
//            dateNext = calNext.getTime();

            for (AlarmModel model : alarmList) {
                if (model.isActive) {
                    Log.d(String.valueOf(model.uniqueID), "Alarm: " + model.hour + ":" + model.minute);
                    calModel.set(Calendar.HOUR_OF_DAY, Integer.parseInt(model.hour));
                    calModel.set(Calendar.MINUTE, Integer.parseInt(model.minute));
                    calModel.set(Calendar.SECOND, 0);
                    calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
                    dateModel = calModel.getTime();

                    if (model.once) {
                        Log.d(String.valueOf(model.uniqueID), "model.once");
                        if (dateModel.before(dateNow)) {
                            calModel.add(Calendar.DAY_OF_YEAR, 1);
                            dateModel = calModel.getTime();
                        }
                    } else {
                        logModel(model);

                        do {
                            Log.d(String.valueOf(model.uniqueID), "do-while");

                            dateModel = calModel.getTime();

                            if (isDayActive(calModel.get(Calendar.DAY_OF_WEEK), model)) {

                                Log.d(String.valueOf(model.uniqueID), "Day Of Year: " +
                                        String.valueOf(calModel.get(Calendar.DAY_OF_YEAR)));

                                if (dateModel.after(dateNow)) {
                                    dateModel = calModel.getTime();
                                    Log.d(String.valueOf(model.uniqueID), " -- break; --");
                                    break;
                                }
                            }

                            calModel.add(Calendar.DAY_OF_YEAR, 1);

                            Log.d(String.valueOf(model.uniqueID), "DAY_OF_YEAR ++");
                            Log.d(String.valueOf(model.uniqueID), "dateModel.before(dateNext) = " +
                                    String.valueOf(dateModel.before(dateNext)));

                        } while (dateModel.before(dateNext));

                        Log.d(String.valueOf(model.uniqueID), "Exit: " +
                                String.valueOf(calModel.get(Calendar.DAY_OF_WEEK)));

                    }
                    Log.d(String.valueOf(model.uniqueID), "dateNext: " +
                            String.valueOf(dateModel.after(dateNow) && dateModel.before(dateNext)));
                    Log.d("MODEL", dateModel.toString());
                    dateNext = dateModel.after(dateNow) && dateModel.before(dateNext) ? dateModel : dateNext;
                }
            }
            logNextAlarm(dateNext, dateNow);
            return dateNext.getTime();
        }
        else {
            Log.d("getNextAlarmMillis()", String.valueOf(returnMillis != NO_ALARMS) + " && " +
                    String.valueOf(alarmList.size() > 0));
        }
        return NO_ALARMS;
    }

    private void logNextAlarm(Date next, Date now) {
        Log.d("NEXT", next.toString());
        Log.d("NOW", now.toString());
    }

    private boolean isDayActive(int dayOfWeek, AlarmModel model) {
        boolean active = false;
        String dayOfWeekString = "oops! something went wrong";
        if (dayOfWeek == Calendar.SUNDAY) {
            active = model.sunday;
            dayOfWeekString = "sunday";
        }
        else if (dayOfWeek == Calendar.MONDAY) {
            active = model.monday;
            dayOfWeekString = "monday";
        }
        else if (dayOfWeek == Calendar.TUESDAY) {
            active = model.tuesday;
            dayOfWeekString = "tuesday";
        }
        else if (dayOfWeek == Calendar.WEDNESDAY) {
            active = model.wednesday;
            dayOfWeekString = "wednesday";
        }
        else if (dayOfWeek == Calendar.THURSDAY) {
            active = model.thursday;
            dayOfWeekString = "thursday";
        }
        else if (dayOfWeek == Calendar.FRIDAY) {
            active = model.friday;
            dayOfWeekString = "friday";
        }
        else if (dayOfWeek == Calendar.SATURDAY) {
            active = model.saturday;
            dayOfWeekString = "saturday";
        }
        Log.d(String.valueOf(model.uniqueID), "isDayActive: " + dayOfWeekString + ", " + String.valueOf(active));
        return active;
    }

    private void logModel(AlarmModel model) {
        Log.d(String.valueOf(model.uniqueID), "model repeats:");

        if(model.sunday) {
            Log.d(String.valueOf(model.uniqueID), "sunday");
        }
        if(model.monday) {
            Log.d(String.valueOf(model.uniqueID), "monday");
        }
        if(model.tuesday) {
            Log.d(String.valueOf(model.uniqueID), "tuesday");
        }
        if(model.wednesday) {
            Log.d(String.valueOf(model.uniqueID), "wednesday");
        }
        if(model.thursday) {
            Log.d(String.valueOf(model.uniqueID), "thursday");
        }
        if(model.friday) {
            Log.d(String.valueOf(model.uniqueID), "friday");
        }
        if(model.saturday) {
            Log.d(String.valueOf(model.uniqueID), "saturday");
        }
        Log.d(String.valueOf(model.uniqueID), " ");
    }
}*/
