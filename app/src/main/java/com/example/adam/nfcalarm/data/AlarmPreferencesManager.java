package com.example.adam.nfcalarm.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AlarmPreferencesManager {

    private static final String PREF_NAME = AlarmPreferencesManager.class.getSimpleName();
    private static final String KEY_VALUE = "com.example.adam.nfcalarm.data.KEY_VALUE";

    private static AlarmPreferencesManager sInstance;
    private final SharedPreferences mPref;

    private AlarmPreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AlarmPreferencesManager(context);
        }
    }

    public static synchronized AlarmPreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(AlarmPreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(...) method first.");
        }
        return sInstance;
    }

}