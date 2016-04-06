package com.example.adam.nfcalarm.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = PreferencesManager.class.getSimpleName();

    public static final String KEY_VALUE_ALARMS = "com.example.adam.nfcalarm.data.KEY_VALUE_ALARMS";
    private static final String KEY_VALUE_MILLIS = "com.example.adam.nfcalarm.data.KEY_VALUE_MILLIS";
    private static final String KEY_VALUE_ID = "com.example.adam.nfcalarm.data.KEY_VALUE_ID";

    private static PreferencesManager sInstance;
    private final Context mContext;
    private final SharedPreferences mPref;

    private final SharedPreferences.OnSharedPreferenceChangeListener mListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals(KEY_VALUE_ALARMS)) {
                        // TODO: 16-04-05 write millis of next alarm to KEY_VALUE_MILLIS
                        // TODO: 16-04-05 write ID of next alarm to KEY_VALUE_ID
                    }
                }
            };

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
}