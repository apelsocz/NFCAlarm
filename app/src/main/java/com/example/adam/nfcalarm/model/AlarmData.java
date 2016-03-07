package com.example.adam.nfcalarm.model;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.widget.Toast;
import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.util.Views;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AlarmData {
    public static final String ALARM_NEXT = "com.example.adam.nfcalarm.model.NEXT_ALARM";
    public static final long NO_ALARMS = 0;

    private SharedPreferences sharedPreferences;
    private JSONArray alarms;
    private Activity appActivity;

/*    public AlarmData(Activity activity) {
        appActivity = activity;
        sharedPreferences = activity.getSharedPreferences(ApplicationActivity.NAME, Context.MODE_PRIVATE);
        //// TODO: 16-01-27 remove instantiation of AlarmData and proceeding readPreferences
        readPreferences();
    }*/

    public AlarmData(Context context) {
        // // TODO: 16-02-19 consider using AlarmData.NAME as sharedpereferences 'name'
        sharedPreferences = context.getSharedPreferences(ApplicationActivity.NAME, Context.MODE_PRIVATE);
        //// TODO: 16-01-27 remove instantiation of AlarmData and proceeding readPreferences
        readPreferences();
    }

    public void setApplicationActivity(Activity activity) {
        appActivity = activity;
    }

    public void setAlarms(JSONArray json) {
        sharedPreferences.edit().putString(ApplicationActivity.ALARM_KEY, json.toString()).apply();
        List<AlarmModel> alarmList = toList();

        // enable / disable WakefulAlarmReceiver via ApplicationActivity
        boolean scheduledAlarm = false;
        for (AlarmModel alarmModel : alarmList) {
            if (alarmModel.isActive) {
                scheduledAlarm = true;
                break;
            }
        }

//        updateNextAlarmMillis();

        if (!Views.isActivityNull(appActivity)){
            ((ApplicationActivity)appActivity).doAlarmSchedule(scheduledAlarm);
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

    private void readPreferences() {
        String alarmsAsString = sharedPreferences.getString(ApplicationActivity.ALARM_KEY, "");

        try {
            alarms = new JSONArray(alarmsAsString);
        } catch (JSONException e) {
            e.printStackTrace();
            alarms = new JSONArray();
        }
    }

    public long getNextAlarmInMillis() {
        Log.d("getNextAlarmMillis()", "Calling method");
        // TODO: 16-02-21 retMillis ? sharedPreferences : 0
        long retMillis = 0L;
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
        //TODO potential dateModel isn't assigned - maybe surround everything, if alarmList.size > 0
        // this may not happen, code is expecting an Alarm to be set as trigger
//        Date dateModel = alarmList.size() > 0 ? new Date(/**/) : new Date(0L);
        

        //// TODO: 16-03-02 temporary setting date of Next Alarm for testing
        calNext.add(Calendar.DAY_OF_YEAR, 5);
        dateNext = calNext.getTime();

        for (AlarmModel model : alarmList) {
            if (model.isActive) {
                Log.d(String.valueOf(model.uniqueID), "Alarm: " + model.hour + ":" + model.minute);
                calModel.set(Calendar.HOUR_OF_DAY, Integer.parseInt(model.hour));
                calModel.set(Calendar.MINUTE, Integer.parseInt(model.minute));
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
                        Log.d(String.valueOf(model.uniqueID), "\n do-while");
                        if (isDayActive(calModel.get(Calendar.DAY_OF_WEEK), model)) {

                            Log.d(String.valueOf(model.uniqueID), "Day Of Year: " +
                                    String.valueOf(calModel.get(Calendar.DAY_OF_YEAR)));

                            dateModel = calModel.getTime();
                            if (dateModel.after(dateNow)) {
                                Log.d(String.valueOf(model.uniqueID), " -- break; --");
                                break;
                            }
                        }

                        calModel.add(Calendar.DAY_OF_YEAR, 1);
                        dateModel = calModel.getTime();

                        Log.d(String.valueOf(model.uniqueID), "DAY_OF_YEAR ++");
                        Log.d(String.valueOf(model.uniqueID), "dateModel.before(dateNext) = " +
                                String.valueOf(dateModel.before(dateNext)));

                    } while (dateModel.before(dateNext));

                    Log.d(String.valueOf(model.uniqueID), "Exit: " +
                            String.valueOf(calModel.get(Calendar.DAY_OF_WEEK)));

                }
                // assign dateNext, if after dateNow && before dateNext
                Log.d(String.valueOf(model.uniqueID), "dateNext: " +
                        String.valueOf(dateModel.after(dateNow) && dateModel.before(dateNext)));
                dateNext = dateModel.after(dateNow) && dateModel.before(dateNext) ? dateModel : dateNext;
            }
        }

        return dateNext.getTime();
        //// TODO: 16-03-06 return ternary statement, psf NO_ALARMS, if there are no alarms
//        return dateNext != null ? dateNext.getTime() : NO_ALARMS;
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

    public void alarmMillisTest() {
/*        Log.d("Calling method", "getNextAlarmMillis()");
        //// TODO: 16-02-21 retMillis ? sharedPreferences : 0
        long retMillis = 0L;

        List<AlarmModel> alarmList = toList();

        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(System.currentTimeMillis());
        long rightNowMillis = rightNow.getTimeInMillis();

        Calendar modelCalendar = Calendar.getInstance();
        modelCalendar.setTimeInMillis(System.currentTimeMillis());

        for (AlarmModel model : alarmList) {
            if (model.isActive) {
                Log.d(String.valueOf(model.uniqueID), model.hour + ":" + model.minute);
                modelCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(model.hour));
                modelCalendar.set(Calendar.MINUTE, Integer.parseInt(model.minute));
                long modelMillis = modelCalendar.getTimeInMillis();

                if (model.once) {
                    if (rightNowMillis >= modelMillis) {
                        modelCalendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    // determine return value
                    rightNow.add(Calendar.SECOND, 15);
                    retMillis = rightNow.getTimeInMillis();
                    Log.d(String.valueOf(model.uniqueID), "-> model.once");
                } else {
                    logModel(model);

                }
            }
        }

        return retMillis;*/
    }
}



/*    public long getNextAlarmInMillis() {
        Log.d("getNextAlarmMillis()", "Calling method");
        // TODO: 16-02-21 retMillis ? sharedPreferences : 0
        long retMillis = 0L;
        Calendar calNext = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calModel = Calendar.getInstance();
        calNow.setTimeInMillis(System.currentTimeMillis());
        calModel.setTimeInMillis(System.currentTimeMillis());
        calNext.setTimeInMillis(System.currentTimeMillis());
        Date dateNext;
        Date dateNow = calNow.getTime();
        List<AlarmModel> alarmList = toList();
        Date dateModel;
        //TODO potential dateModel isnt assigned - maybe surround everything, if alarmList.size > 0
        // this may not happen, code is expecting an Alarm to be set as trigger
//        Date dateModel = alarmList.size() > 0 ? new Date(*//**//*) : new Date(0L);

        //// TODO: 16-03-02 temporary setting date of Next Alarm for testing
        calNext.add(Calendar.DAY_OF_YEAR, 2);
        dateNext = calNext.getTime();

        for (AlarmModel model : alarmList) {
            if (model.isActive) {
                Log.d(String.valueOf(model.uniqueID), "Alarm: " + model.hour + ":" + model.minute);
                calModel.set(Calendar.HOUR_OF_DAY, Integer.parseInt(model.hour));
                calModel.set(Calendar.MINUTE, Integer.parseInt(model.minute));
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
                        Log.d(String.valueOf(model.uniqueID), "DAY_OF_WEEK: " + String.valueOf(
                                calModel.get(Calendar.DAY_OF_WEEK)));

                        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) && model.sunday ) {
                            dateModel = calModel.getTime();
                            Log.d(String.valueOf(model.uniqueID), "sun");
                        }
                        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) && model.monday ) {
                            dateModel = calModel.getTime();
                            Log.d(String.valueOf(model.uniqueID), "mon");
                        }
                        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) && model.tuesday ) {
                            dateModel = calModel.getTime();
                            Log.d(String.valueOf(model.uniqueID), "tue");
                        }
                        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) && model.wednesday ) {
                            dateModel = calModel.getTime();
                            Log.d(String.valueOf(model.uniqueID), "wed");
                        }
                        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) && model.thursday ) {
                            dateModel = calModel.getTime();
                            Log.d(String.valueOf(model.uniqueID), "thu");
                        }
                        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) && model.friday ) {
                            dateModel = calModel.getTime();
                            Log.d(String.valueOf(model.uniqueID), "fri");
                        }
                        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) && model.saturday ) {
                            dateModel = calModel.getTime();
                            Log.d(String.valueOf(model.uniqueID), "sat");
                        }

                        calModel.add(Calendar.DAY_OF_YEAR, 1);
                        Log.d(String.valueOf(model.uniqueID), "DAY_OF_YEAR++");
                        Log.d(String.valueOf(model.uniqueID), String.valueOf(dateModel.before(dateNext)));
                    } while (dateModel.before(dateNow));
//                    } while (dateModel.before(dateNext));
                }
                // assign dateNext, if after dateNow && before dateNext
                dateNext = dateModel.after(dateNow) && dateModel.before(dateNext) ? dateModel : dateNext;
            }
        }

        return dateNext.getTime();
    }*/







/*
if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ) {
        if (model.sunday){
        dateModel = calModel.getTime();
        Log.d(String.valueOf(model.uniqueID), "sun");
        }
        }
        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) ) {
        if (model.sunday){
        dateModel = calModel.getTime();
        Log.d(String.valueOf(model.uniqueID), "mon");
        }
        }
        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)  ) {
        if (model.sunday){
        dateModel = calModel.getTime();
        Log.d(String.valueOf(model.uniqueID), "tue");
        }
        }
        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) ) {
        if (model.sunday){
        dateModel = calModel.getTime();
        Log.d(String.valueOf(model.uniqueID), "wed");
        }
        }
        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) ) {
        if (model.sunday){
        dateModel = calModel.getTime();
        Log.d(String.valueOf(model.uniqueID), "thu");
        }
        }
        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) ) {
        if (model.sunday){
        dateModel = calModel.getTime();
        Log.d(String.valueOf(model.uniqueID), "fri");
        }
        }
        if ( (calModel.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ) {
        if (model.sunday){
        dateModel = calModel.getTime();
        Log.d(String.valueOf(model.uniqueID), "sat");
        }
        }*/
