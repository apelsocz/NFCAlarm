package com.example.adam.nfcalarm;

import android.app.Application;

import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.scheduler.NfcStateReceiver;
import com.example.adam.nfcalarm.scheduler.WakefulAlarmReceiver;


public class MyApplication extends Application {

    private static MyApplication mInstance;
    private WakefulAlarmReceiver mAlarmReceiver = new WakefulAlarmReceiver();
    private NfcStateReceiver mNfcState = new NfcStateReceiver();
    private AlarmDAO mAlarmDAO;
    public boolean isRinging = false;
    public boolean isSnoozing = false;

    public static MyApplication getInstance() {
        return mInstance;
    }

    // TODO: 16-04-08 add prompt - would you like to repeat this tomorrow? if model.once
    // TODO: 16-04-11 show notification when user leaves the app

    /*
     *  Called when the application is starting,
     *  before any other application objects have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mAlarmDAO = new AlarmDAO();
    }

    public void doScheduling(boolean schedule) {
        if (schedule) {
            mAlarmReceiver.setAlarm(this);
        }
        else if (!schedule) {
            mAlarmReceiver.cancelAlarm(this);
        }
    }

    public void setmNfcStateReceiver(boolean enabled) {
        mNfcState.setEnabled(this, enabled);
    }

    public void snooze() {
        mAlarmReceiver.snooze(this);
        isSnoozing = true;
    }

    public void setSnoozing(boolean isRinging) {
        this.isSnoozing = isRinging;
    }

    public void dismiss() {
        isRinging = false;
    }
}