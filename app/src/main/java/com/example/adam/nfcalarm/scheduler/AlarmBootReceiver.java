package com.example.adam.nfcalarm.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by adam on 16-01-17.
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    WakefulAlarmReceiver alarm = new WakefulAlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            alarm.setAlarm(context);
        }
    }
}
