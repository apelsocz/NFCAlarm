package com.example.adam.nfcalarm;

import android.app.Application;
import android.content.res.Configuration;

import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.data.AlarmDataManager;
import com.example.adam.nfcalarm.scheduler.WakefulAlarmReceiver;


public class MyApplication extends Application {

    private static MyApplication mInstance;
    private WakefulAlarmReceiver alarmReceiver = new WakefulAlarmReceiver();
    private AlarmDAO mAlarmDAO;
    private boolean isRinging = false;
    private boolean isSnoozing = false;

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
            alarmReceiver.setAlarm(this);
        }
        else if (!schedule) {
            alarmReceiver.cancelAlarm(this);
        }
    }

    public void snooze() {
        alarmReceiver.snooze(this);
        isSnoozing = true;
    }

    public void dismiss() {
        isRinging = false;

    }

    public boolean getRinging() {
        return isRinging;
    }

    public void setRinging(boolean isRinging) {
        this.isRinging = isRinging;
    }

    public boolean getSnoozing() {
        return isSnoozing;
    }

    public void setSnoozing(boolean isRinging) {
        this.isSnoozing = isRinging;
    }
}