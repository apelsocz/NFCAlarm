package com.example.adam.nfcalarm.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.adam.nfcalarm.MyApplication;
import com.example.adam.nfcalarm.model.AlarmModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmDataManager {

    //done
    private static final String PREF_NAME = AlarmDataManager.class.getSimpleName();
    //done
    private static final String KEY_VALUE_ALARMS = "com.example.adam.nfcalarm.data.KEY_VALUE_ALARMS";
    //done
    private static final String KEY_VALUE_MILLIS = "com.example.adam.nfcalarm.data.KEY_VALUE_MILLIS";
    //done
    private static final String KEY_VALUE_ID = "com.example.adam.nfcalarm.data.KEY_VALUE_ID";

    //done
    private static AlarmDataManager sInstance;
    //done
    private final Context mContext;
    //done
    private final SharedPreferences mPref;

    //done
    private AlarmDataManager(Context context) {
        mContext = context;
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    //done
    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            // ApplicationContext is a singleton instance running in the application PID.
            sInstance = new AlarmDataManager(context.getApplicationContext());
        }
    }

    //done
    public static synchronized AlarmDataManager getInstance() throws IllegalStateException {
        if (sInstance == null) {
            throw new IllegalStateException(AlarmDataManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(...) method first.");
        }
        return sInstance;
    }

    // TODO: 16-04-03
    public void doAlarmDismissed(long id) {
        List<AlarmModel> list = getAlarmsList();
        for (AlarmModel model : list) {
            if (model.uniqueID == id) {
                if (model.once && model.isActive) {
                    int index = list.indexOf(model);
                    AlarmModel updateModel = new AlarmModel(model.uniqueID, !model.isActive, model.hour,
                            model.minute, model.once, model.sunday, model.monday, model.tuesday,
                            model.wednesday, model.thursday, model.friday, model.saturday);
                    updateModelAtIndex(updateModel, index);
                }
            }
        }
        update();
    }

    // TODO: 16-04-03  AlarmDAO
    public AlarmModel getAlarmModelByID(long id) {
        AlarmModel model;
        List<AlarmModel> modelList = getAlarmsList();
        for (AlarmModel alarmModel : modelList) {
            if (alarmModel.uniqueID == id) {
                return alarmModel;
            }
        }
        //// TODO: 16-04-05 return AlarmModel.EMPTY | null ...(model.isEmpty)
        return null;
    }

    // TODO: 16-04-03  AlarmDAO
    public boolean updateModelAtIndex(AlarmModel model, int index) {
        boolean updated = false;
        JSONArray before = getJSONArray();

        if (before.length() > 0) {
            JSONArray after;
            try {
                after = before.put(index, model.json);
                updated = setPreferencesAlarmsData(after.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return updated;
    }

    //done
    public void updateAlarmsData(JSONArray json) {
        setPreferencesAlarmsData(json.toString());
    }

    //done
    public long getNextAlarmMillis() {
        return getPreferencesNextMillis();
    }

    //done
    private long getPreferencesNextMillis() {
        return mPref.getLong(KEY_VALUE_MILLIS, 0L);
    }

    //done
    @SuppressLint("CommitPrefEdits")
    private boolean setPreferencesNextMillis(Long millis) {
        return mPref.edit()
                .putLong(KEY_VALUE_MILLIS, millis)
                .commit();
    }

    //done
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

    //done
    public JSONArray getJSONArray() {
        String modelsAsString = getPreferencesAlarmsData();
        JSONArray modelsAsJSON;

        try {
            modelsAsJSON = new JSONArray(modelsAsString);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(PREF_NAME + " getJSONArray()", "modelsAsString is empty");
            modelsAsJSON = new JSONArray();
        }
        return modelsAsJSON;
    }

    //done
    private String getPreferencesAlarmsData() {
        return mPref.getString(KEY_VALUE_ALARMS, "");
    }

    //done
    @SuppressLint("CommitPrefEdits")
    private boolean setPreferencesAlarmsData(String string) {
        boolean updated = false;
        updated = mPref.edit()
                .putString(KEY_VALUE_ALARMS, string)
                .commit();

        update();

        return updated;
    }

    //// TODO: 16-04-03
    private void update() {
        //set to zero to force search - takes care of active alarm being deleted from list
        setPreferencesNextMillis(0L);
        long millis = findNextAlarmMillis();
        setPreferencesNextMillis(millis);

        MyApplication app = (MyApplication) mContext;
        app.doScheduling(containsActiveAlarm());
    }

    //done
    public long getNextAlarmID() {
        return getPreferencesNextID();
    }

    private long getPreferencesNextID() {
        return mPref.getLong(KEY_VALUE_ALARMS, 0);
    }

    //done
    @SuppressLint("CommitPrefEdits")
    private boolean setPreferencesNextID(long id) {
        return mPref.edit()
                .putLong(KEY_VALUE_ID, id)
                .commit();
    }

    //// TODO: 16-04-03
    private long findNextAlarmMillis() {
        long nextModelID = 0L;
        Calendar calNext = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calModel = Calendar.getInstance();

        calNext.setTimeInMillis(getPreferencesNextMillis());
        calNow.setTimeInMillis(System.currentTimeMillis());
        calModel.setTimeInMillis(System.currentTimeMillis());

        Date dateNext = calNext.getTime();
        Date dateNow = calNow.getTime();
        Date dateModel;

        Log.d("[START] now", dateNow.toString());
        Log.d("[START] next", String.valueOf(dateNext.getTime()));
        Log.d("[START] next", dateNext.toString());

        List<AlarmModel> alarmList = getAlarmsList();

        if (alarmList.size() > 0) {
            if (containsActiveAlarm(alarmList)) {
                for (AlarmModel model : alarmList) {
                    if (model.isActive) {
                        String tag = String.valueOf(model.uniqueID) + ", [" + model.hour + ":" + model.minute + "]";
                        Log.d(tag, "<- isActive");
                        calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
                        calModel.set(Calendar.HOUR_OF_DAY, 0);
                        calModel.set(Calendar.MINUTE, 0);
                        // allow for 5 seconds to be flexible
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
                            nextModelID = model.uniqueID;
                        }
                        else {
                            //assign, if dateModel is after dateNow && before current assignment
                            Log.d(tag, "dateNext.getTime() != 0");
                            dateNext = dateModel.after(dateNow) && dateModel.before(dateNext) ?
                                    dateModel : dateNext;
                            nextModelID = dateNext.compareTo(dateModel) == 0 ? model.uniqueID : nextModelID;
                        }

                        setPreferencesNextID(nextModelID);
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

    //// TODO: 16-04-03
    private boolean containsActiveAlarm() {
        return containsActiveAlarm(getAlarmsList());
    }

    //// TODO: 16-04-03
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

    //// TODO: 16-04-03
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

    //// TODO: 16-04-03
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

    //done
    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    //done
    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}