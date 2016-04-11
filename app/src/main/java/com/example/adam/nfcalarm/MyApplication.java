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

    public static MyApplication getInstance() {
        return mInstance;
    }

    //// TODO: 16-04-08 add prompt - would you like to repeat this tomorrow? if model.once

    /*
     *  Called when the application is starting,
     *  before any other application objects have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AlarmDataManager.initializeInstance(this);
        AlarmDataManager.getInstance().clear();
        mAlarmDAO = new AlarmDAO();
    }

    public boolean getAlarmRinging() {
        return isRinging;
    }

    public void setAlarmRinging(boolean isRinging) {
        this.isRinging = isRinging;
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
    }
}