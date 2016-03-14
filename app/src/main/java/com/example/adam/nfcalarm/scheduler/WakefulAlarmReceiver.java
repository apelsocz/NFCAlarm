package com.example.adam.nfcalarm.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDataManager;
//import com.example.adam.nfcalarm.model.AlarmData;

import java.util.Calendar;
import java.util.Date;
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

    public void onReceive(Context context, Intent intent) {
        Log.d("WakefulAlarmReceiver", "{onReceive}");
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
        Log.d("WakefulAlarmReceiver", "{setAlarm}");

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        AlarmDataManager alarmManager = AlarmDataManager.getInstance();
        long millis = alarmManager.getNextAlarmInMillis();
//        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, millis, alarmIntent);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        Date date = calendar.getTime();
        Log.d("WakefulAlarmReceiver", date.toString());
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);

        // Enable {@code AlarmBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     * @param context the context of the app's Activity
     */
    public void cancelAlarm(Context context) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        alarmMgr.cancel(alarmIntent);

        // Disable {@code AlarmBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}