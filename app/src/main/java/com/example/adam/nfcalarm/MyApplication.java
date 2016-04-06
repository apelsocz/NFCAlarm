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

    public void setAlarmRinging(boolean b) {
        isRinging = b;
    }

    public void doScheduling(boolean scheduleAlarm) {
        if (scheduleAlarm) {
            alarmReceiver.setAlarm(this);
        }
        else if (!scheduleAlarm) {
            alarmReceiver.cancelAlarm(this);
        }
    }

    public void snooze() {
        alarmReceiver.snooze(this);
    }
}