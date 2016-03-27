package com.example.adam.nfcalarm.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Views;

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
    private final Context mContext;
    private final SharedPreferences mPref;

    private AlarmDataManager(Context context) {
        mContext = context;
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

    public long getNextMillisValue() {
        return getNextKeyValue();
    }

    private String getAlarmKeyValue() {
        return mPref.getString(KEY_VALUE_ALARMS, "");
    }

    private boolean setAlarmKeyValue(String string) {
        boolean updated = false;
        updated = mPref.edit()
                    .putString(KEY_VALUE_ALARMS, string)
                    .commit();

        //set to zero to force search - takes care of active alarm being deleted from list
        setNextKeyValue(0L);
        long nextKey = findNextKey();
        setNextKeyValue(nextKey);

        // setAlarmKeyValue should only be called within lifecycle
        ApplicationActivity activity = (ApplicationActivity) mContext;
        if (!Views.isActivityNull(activity)) {
            activity.doScheduling(containsActiveAlarm());
        }

        return updated;
    }

    private long findNextKey() {
        Calendar calNext = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calModel = Calendar.getInstance();

        calNext.setTimeInMillis(getNextKeyValue());
        calNow.setTimeInMillis(System.currentTimeMillis());
        calModel.setTimeInMillis(System.currentTimeMillis());

        Date dateNext = calNext.getTime();
        Date dateNow = calNow.getTime();
        Date dateModel;

        Log.d("[START] now", dateNow.toString());
        Log.d("[START] next", String.valueOf(dateNext.getTime()));
        Log.d("[START] next", dateNext.toString());

        List<AlarmModel> alarmList = getAlarmsList();

        if (alarmList.size() > 0){
            if (containsActiveAlarm(alarmList)) {
                for (AlarmModel model : alarmList) {
                    if (model.isActive) {
                        String tag = String.valueOf(model.uniqueID) + ", [" + model.hour + ":" + model.minute + "]";
                        Log.d(tag, "<- isActive");
                        calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
                        calModel.set(Calendar.HOUR_OF_DAY, 0);
                        calModel.set(Calendar.MINUTE, 0);
                        // set to 5 seconds to flex for midnight calculations and changes to leap seconds
                        calModel.set(Calendar.SECOND, 5);
                        dateModel = calModel.getTime();
                        Log.d(tag, "<- refreshed (" + dateModel.toString() + ")");
                        int hourOfDay = Integer.parseInt(model.hour);
                        int minute = Integer.parseInt(model.minute);
                        Log.d(tag, String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                        calModel.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calModel.set(Calendar.MINUTE, minute);

                        dateModel = calModel.getTime();
                        Log.d(tag, "<- add (" + dateModel.toString() + ")");

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
                        if (dateNext.getTime() == 0L) {
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
                    }
                }
            }
            else {
                Log.d("[RETURN]", "early, there are no active alarms");
                return 0L;
            }
        }
        else {
            Log.d("[RETURN]", "early, alarm list is empty");
            return 0L;
        }

        Log.d("[RETURN] getTime", String.valueOf(dateNext.getTime()));
        Log.d("[RETURN] dateNext", dateNext.toString());
        return dateNext.getTime();
    }

    private long getNextKeyValue() {
        return mPref.getLong(KEY_VALUE_NEXT, 0L);
    }

    private boolean setNextKeyValue(Long millis) {
        return mPref.edit()
                .putLong(KEY_VALUE_NEXT, millis)
                .commit();
    }

    private boolean containsActiveAlarm() {
        return containsActiveAlarm(getAlarmsList());
    }

    private boolean containsActiveAlarm(List<AlarmModel> list) {
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

/*    private void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    private boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }*/
}