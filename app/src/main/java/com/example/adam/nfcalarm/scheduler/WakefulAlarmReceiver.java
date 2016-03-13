package com.example.adam.nfcalarm.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.R;
//import com.example.adam.nfcalarm.model.AlarmData;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code AlarmSchedulingService} to do some work.
 */
public class WakefulAlarmReceiver extends WakefulBroadcastReceiver {

    // provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    private boolean millisSet;

    public void onReceive(Context context, Intent intent) {
        // simply create a new intent to deliver to the intent service.
        Intent service = new Intent(context, AlarmSchedulingService.class);
        startWakefulService(context, service);
    }

    /**
     * Sets the next alarm to run. When the alarm fires, the app broadcasts an Intent to this
     * WakefulBroadcastReceiver.
     * @param context the context of the app's Activity
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        AlarmData alarmData = new AlarmData(context);
//        long nextAlarmMillis = alarmData.setNextAlarm(AlarmData.ALARM_SET);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 10);


        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
//        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, nextAlarmMillis, alarmIntent);
//        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmData.getNextAlarmInMillisTroubleshooting(), alarmIntent);

        // Enable {@code AlarmBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {
        if(alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
//        AlarmData alarmData = new AlarmData(context);
//        alarmData.setNextAlarm(AlarmData.ALARM_CANCEL);

        // Disable {@code AlarmBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
