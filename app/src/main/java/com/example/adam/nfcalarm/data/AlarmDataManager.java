package com.example.adam.nfcalarm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.adam.nfcalarm.model.AlarmModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmDataManager {

    private static final String PREF_NAME = AlarmDataManager.class.getSimpleName();
    private static final String KEY_VALUE_ALARMS = "com.example.adam.nfcalarm.data.KEY_VALUE_ALARMS";
    private static final String KEY_VALUE_NEXT = "com.example.adam.nfcalarm.data.KEY_VALUE_NEXT";

    private static AlarmDataManager sInstance;
    private final SharedPreferences mPref;

    private AlarmDataManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AlarmDataManager(context);
        }
    }

    public static synchronized AlarmDataManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(AlarmDataManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(...) method first.");
        }
        return sInstance;
    }

    public JSONArray getJSONArray() {
        String modelsAsString = getAlarmKeyValue();
        JSONArray modelsAsJSON;

        try {
            modelsAsJSON = new JSONArray(modelsAsString);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(PREF_NAME + " getJSONArray()", "modelsAsString is empty");
            modelsAsJSON = new JSONArray();
        }
        return modelsAsJSON;
    }

    public List<AlarmModel> getAlarmsList() {
        List<AlarmModel> modelList = new ArrayList<>();
        JSONArray models = getJSONArray();

        if (models.length() > 0) {
            int len = models.length();
            for (int i = 0; i < len; i++) {
                modelList.add(new AlarmModel(models.optString(i)));
            }
        }

        return modelList;
    }

    public boolean doIndexUpdate(AlarmModel model, int index) {
        boolean updated = false;
        JSONArray before = getJSONArray();

        if (before.length() > 0) {
            JSONArray after;
            try {
                after = before.put(index, model.json);
                updated = setAlarmKeyValue(after.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return updated;
    }

    public void doUpdate(JSONArray json) {
        setAlarmKeyValue(json.toString());
    }

    private String getAlarmKeyValue() {
        return mPref.getString(KEY_VALUE_ALARMS, "");
    }

    private boolean setAlarmKeyValue(String string) {
        boolean updated = false;
        updated = mPref.edit()
                    .putString(KEY_VALUE_ALARMS, string)
                    .commit();
//        setNextKeyValue(doNextKey());
        setNextKeyValue(findNextKey());
        return updated;
    }

    private long doNextKey() {
        long nextMillis = getNextKeyValue();

        Calendar calNext = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calModel = Calendar.getInstance();
        calNext.setTimeInMillis(System.currentTimeMillis());
        calNow.setTimeInMillis(System.currentTimeMillis());
        calModel.setTimeInMillis(System.currentTimeMillis());
        Date dateNext;
        Date dateNow = calNow.getTime();
        Date dateModel;
        List<AlarmModel> alarmList = getAlarmsList();

//        //// TODO: 16-03-02 temporary setting date of Next Alarm for testing
//        calNext.add(Calendar.DAY_OF_YEAR, 7);
//        dateNext = calNext.getTime();

//        if (nextMillis != 0 && alarmList.size() > 0 ) {
        if (alarmList.size() > 0 ) {
            calNext.setTimeInMillis(nextMillis);
            dateNext = calNext.getTime();

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
//                                    break;
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
            Log.d("containsActiveAlarm()", String.valueOf(containsActiveAlarm(alarmList)));
            nextMillis = containsActiveAlarm(alarmList) ? dateNext.getTime() : 0;
        }
        else {
//            Log.d("getNextAlarmMillis()", String.valueOf(nextMillis != 0) + " && " +
//                    String.valueOf(alarmList.size() > 0));
            Log.d("getNextAlarmMillis()", String.valueOf(alarmList.size() > 0));
            nextMillis = 0;
        }

        Log.d("NEXTMILLIS", String.valueOf(nextMillis));
        return nextMillis;
    }

    private long findNextKey() {
        long nextMillis = getNextKeyValue();
        Log.d("nextMillis", String.valueOf(nextMillis));

        Calendar calNext = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calModel = Calendar.getInstance();
        calNext.setTimeInMillis(nextMillis);
        calNow.setTimeInMillis(System.currentTimeMillis());
        calModel.setTimeInMillis(System.currentTimeMillis());
        Date dateNext = calNext.getTime();
        Date dateNow = calNow.getTime();
        Date dateModel = calModel.getTime();

        List<AlarmModel> alarmList = getAlarmsList();

        if (alarmList.size() > 0){
            if (containsActiveAlarm(alarmList)) {
                for (AlarmModel model : alarmList) {
                    if (model.isActive) {
                        String tag = String.valueOf(model.uniqueID) + ", [" + model.hour + ":" + model.minute + "]";
                        Log.d(tag, "<- isActive");
                        calModel.set(Calendar.HOUR_OF_DAY, Integer.parseInt(model.hour));
                        calModel.set(Calendar.MINUTE, Integer.parseInt(model.minute));
                        calModel.set(Calendar.SECOND, 0);
                        calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
                        dateModel = calModel.getTime();

                        if (model.once) {
                            Log.d(tag, "model.once");
                            if (dateModel.before(dateNow)) {
                                calModel.add(Calendar.DAY_OF_YEAR, 1);
                                dateModel = calModel.getTime();
                            }
                        }
                        else {
                            Log.d(tag, "model.repeats");
                            // iterate, until first occurence of: dateModel being active after dateNow
                            int i = 0;
                            boolean iterate = true;
                            do {
                                Log.d(tag, "Iteration [" + String.valueOf(i) + "]");
                                // refresh model calendar to today and increment by iteration value
                                calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
                                calModel.add(Calendar.DAY_OF_YEAR, i);
                                dateModel = calModel.getTime();

                                if (isDayActive(calModel.get(Calendar.DAY_OF_WEEK), model)) {
                                Log.d(tag, "> " + formatDayOfYear(calModel.get(Calendar.DAY_OF_WEEK)) +
                                        " " + String.valueOf(calModel.get(Calendar.DAY_OF_YEAR))
                                        + ", repeat: true");
                                    // check if model is after now
                                    if (dateModel.after(dateNow)) {
                                        iterate = false;
                                    }
                                }
                                else {
                                    Log.d(tag, "> " + formatDayOfYear(calModel.get(Calendar.DAY_OF_WEEK)) +
                                            " " + String.valueOf(calModel.get(Calendar.DAY_OF_YEAR))
                                            + ", repeat: false");
                                }
                                String msg = iterate ? "keep going" : "STOP!";
                                Log.d(tag, "> " + msg);

                                i++;
                            } while (iterate);
                        }
                        // assign dateNext
                        Log.d(tag, dateModel.toString());
                        if (dateNext.getTime() == 0) {
                            // assign knowing that dateModel is more ideal
                            Log.d(tag, "dateNext.getTime() == 0");
                            dateNext = dateModel;
                        }
                        else {
                            //assign, if dateModel is after dateNow && before current assignment
                            Log.d(tag, "dateNext.getTime() != 0");
                            dateNext = dateModel.after(dateNow) && dateModel.before(dateNext) ?
                                    dateModel : dateNext;
                        }
                        nextMillis = dateNext.getTime();
                    }
                }
            }
            else {
                nextMillis = 0;
            }
        }
        else {
            nextMillis = 0;
        }

        Log.d("[RETURN] nextmillis", String.valueOf(nextMillis));
        Log.d("[RETURN] dateNext", dateNext.toString());
        return nextMillis;
    }

    //// TODO: 16-03-12 reference, when calling public doUpdate()
    public boolean containsActiveAlarm(List<AlarmModel> list) {
        boolean schedule = false;
        for (AlarmModel model : list) {
            if (model.isActive) {
                schedule = true;
                break;
            }
        }
        Log.d("containsActiveAlarm", String.valueOf(schedule));
        return schedule;
    }

    private void logNextAlarm(Date next, Date now) {
        Log.d("NEXT", next.toString());
        Log.d("NOW", now.toString());
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

    private String formatDayOfYear(int dayOfWeek) {
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

    private boolean isDayActive(int dayOfWeek, AlarmModel model) {
        boolean active = false;

        if (dayOfWeek == Calendar.SUNDAY) {
            active = model.sunday;
        }
        else if (dayOfWeek == Calendar.MONDAY) {
            active = model.monday;
        }
        else if (dayOfWeek == Calendar.TUESDAY) {
            active = model.tuesday;
        }
        else if (dayOfWeek == Calendar.WEDNESDAY) {
            active = model.wednesday;
        }
        else if (dayOfWeek == Calendar.THURSDAY) {
            active = model.thursday;
        }
        else if (dayOfWeek == Calendar.FRIDAY) {
            active = model.friday;
        }
        else if (dayOfWeek == Calendar.SATURDAY) {
            active = model.saturday;
        }
        return active;
    }

    private long getNextKeyValue() {
        return mPref.getLong(KEY_VALUE_NEXT, 0);
    }

    private boolean setNextKeyValue(Long millis) {
        return mPref.edit()
                .putLong(KEY_VALUE_NEXT, millis)
                .commit();
    }

    /*
    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
    */
}