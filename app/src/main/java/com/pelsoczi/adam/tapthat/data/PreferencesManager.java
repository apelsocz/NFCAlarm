package com.pelsoczi.adam.tapthat.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    public static final String PREF_NAME = PreferencesManager.class.getSimpleName();

    public static final String KEY_VALUE_ALARMS = "com.pelsoczi.adam.tapthat.data.KEY_VALUE_ALARMS";
    public static final String KEY_VALUE_MILLIS = "com.pelsoczi.adam.tapthat.data.KEY_VALUE_MILLIS";
    public static final String KEY_VALUE_ID = "com.pelsoczi.adam.tapthat.data.KEY_VALUE_ID";

    private static PreferencesManager sInstance;
    private final Context mContext;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mContext = context;
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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
}