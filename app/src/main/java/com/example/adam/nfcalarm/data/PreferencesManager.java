package com.example.adam.nfcalarm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.adam.nfcalarm.MyApplication;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Data;
import com.example.adam.nfcalarm.util.Format;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PreferencesManager {
    private static final String PREF_NAME = PreferencesManager.class.getSimpleName();

    public static final String KEY_VALUE_ALARMS = "com.example.adam.nfcalarm.data.KEY_VALUE_ALARMS";
    public static final String KEY_VALUE_MILLIS = "com.example.adam.nfcalarm.data.KEY_VALUE_MILLIS";
    public static final String KEY_VALUE_ID = "com.example.adam.nfcalarm.data.KEY_VALUE_ID";

    private static PreferencesManager sInstance;
    private final Context mContext;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mContext = context;
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mPref.registerOnSharedPreferenceChangeListener(mListener);
    }

    protected static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            // ApplicationContext is a singleton instance running in the application PID.
            sInstance = new PreferencesManager(context.getApplicationContext());
        }
    }

    protected static synchronized PreferencesManager getInstance() throws IllegalStateException {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(...) method first.");
        }
        return sInstance;
    }

    protected String getKeyValueString(String key) {
        return mPref.getString(key, "");
    }

    protected long getKeyValueLong(String key) {
        return mPref.getLong(key, 0);
    }

    protected boolean setKeyValue(String key, String value) {
        return mPref.edit().putString(key, value).commit();
    }

    protected boolean setKeyValue(String key, long value) {
        return mPref.edit().putLong(key, value).commit();
    }

    protected boolean remove(String key) {
        return mPref.edit().remove(key).commit();
    }

    protected boolean clear() {
        return mPref.edit().clear().commit();
    }

    @Override
    protected void finalize() throws Throwable {
        mPref.unregisterOnSharedPreferenceChangeListener(mListener);
        super.finalize();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals(KEY_VALUE_ALARMS)) {
                        setKeyValue(KEY_VALUE_MILLIS, 0L);
                        setKeyValue(KEY_VALUE_ID, 0L);

                        List<AlarmModel> alarmList = Data.modelsAsList(
                                Data.modelsAsJSON(getKeyValueString(KEY_VALUE_ALARMS)) );

                        findNextAlarm(alarmList);
                        Log.d("mListener - millis", String.valueOf(getKeyValueLong(KEY_VALUE_MILLIS)));
                        Log.d("mListener - id", String.valueOf(getKeyValueLong(KEY_VALUE_ID)));

                        MyApplication app = (MyApplication) mContext;
                        app.doScheduling(Data.activeModelInList(alarmList));
                    }
                }

                private void findNextAlarm(List<AlarmModel> alarmList) {
                    long nextModelID = 0L;
                    Calendar calNext = Calendar.getInstance();
                    Calendar calNow = Calendar.getInstance();
                    Calendar calModel = Calendar.getInstance();

                    calNext.setTimeInMillis(0L);
                    calNow.setTimeInMillis(System.currentTimeMillis());
                    calModel.setTimeInMillis(System.currentTimeMillis());

                    Date dateNext = calNext.getTime();
                    Date dateNow = calNow.getTime();
                    Date dateModel;

                    Log.d("[START] now", dateNow.toString());
                    Log.d("[START] next", String.valueOf(dateNext.getTime()));
                    Log.d("[START] next", dateNext.toString());

                    if (alarmList.size() > 0) {
                        if (Data.activeModelInList(alarmList) ) {
                            for (AlarmModel model : alarmList) {
                                if (model.isActive) {
                                    String tag = String.valueOf(model.uniqueID) + ", [" +
                                            model.hour + ":" + model.minute + "]";
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
                                        // iterate, until first occurence of:
                                        // dateModel being active after dateNow
                                        int i = 0;
                                        boolean iterate = true;
                                        do {
                                            Log.d(tag, "Iteration [" + String.valueOf(i) + "]");
                                            // refresh model calendar to today and increment by i
                                            calModel.set(Calendar.DAY_OF_YEAR, calNow.get(
                                                    Calendar.DAY_OF_YEAR));
                                            calModel.add(Calendar.DAY_OF_YEAR, i);
                                            dateModel = calModel.getTime();

                                            if (Data.isDayActive(calModel.get(Calendar.DAY_OF_WEEK),
                                                    model)) {
                                                Log.d(tag, "> "
                                                        + Format.dayOfYear(calModel.get(Calendar.DAY_OF_WEEK)) +
                                                        " " + String.valueOf(calModel.get(Calendar.DAY_OF_YEAR))
                                                        + ", repeat: true"
                                                );
                                                // check if model is after now
                                                if (dateModel.after(dateNow)) {
                                                    iterate = false;
                                                }
                                            }
                                            else {
                                                Log.d(tag, "> "
                                                        + Format.dayOfYear(calModel.get(Calendar.DAY_OF_WEEK)) +
                                                        " " + String.valueOf(calModel.get(Calendar.DAY_OF_YEAR))
                                                        + ", repeat: false"
                                                );
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
                                }
                            }
                            //write millis and ID to preferences
                            setKeyValue(KEY_VALUE_MILLIS, dateNext.getTime());
                            setKeyValue(KEY_VALUE_ID, nextModelID);
                        }
                        else {
                            Log.d("[ERROR]", "there are no active alarms");
//                            return 0L;
                        }
                    }
                    else {
                        Log.d("[ERROR]", "alarm list is empty");
//                        return 0L;
                    }

                    Log.d("[FOUND] getTime", String.valueOf(dateNext.getTime()));
                    Log.d("[FOUND] dateNext", dateNext.toString());
//                    return dateNext.getTime();
                }
            };
}