package com.pelsoczi.adam.tapthat.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pelsoczi.adam.tapthat.data.AlarmDAO;

import java.util.Calendar;
import java.util.Date;

/**
 * When the alarm fires, this {@code WakefulReceiver} receives the broadcast Intent
 * and then starts the IntentService {@code MediaService} to do some work.
 */
public class WakefulReceiver extends WakefulBroadcastReceiver {

    protected static final String LOG_TAG = WakefulReceiver.class.getSimpleName();

    /** integer minutes the alarm will be snoozed */
    private static final int EXTRA_SNOOZE_MINUTES = 9;

    // provides access to the system alarm services.
    private AlarmManager mAlarmManager;
    // pending intent that is triggered when the alarm fires.
    private PendingIntent mIntent;
    // provides access to data layer
    private AlarmDAO mAlarmDAO;

    public void onReceive(Context context, Intent intent) {
        //Log.d(LOG_TAG, "onReceive()");

        // start the alarm media service
        Intent mpIntent = new Intent(context, MediaService.class);
        mpIntent.setAction(MediaService.ACTION_PLAY);
        context.startService(mpIntent);

        // show the alarm's ring activity
        Intent service = new Intent(context, StartRingingService.class);
        startWakefulService(context, service);
    }

    /**
     * Sets the next alarm to run. When the alarm fires,
     * the app broadcasts an Intent to this WakefulReceiver.
     * @param context the context of the app's Activity.
     */
    public void set(Context context) {
//        Log.d(LOG_TAG, "set()");

        // cancel previously scheduled
        cancel(context);

        // retrieve millis of the next alarm
        mAlarmDAO = new AlarmDAO();
        long millis = mAlarmDAO.scheduledMillis();

        // declare the broadcast Intent for this {@code WakefulReceiver}
        Intent intent = new Intent(context, WakefulReceiver.class);
        mIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        /*  // for development purposes
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 4);
            Date date = calendar.getTime();
            Log.d("WakefulReceiver", date.toString());
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), mIntent);
         */

        // schedule the broadcast Intent received by this {@code WakefulReceiver#onReceive}
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, mIntent);

        // enable {@code BootReceiver} to automatically reschedule the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the next alarm from running.
     * Removes any intents set by this {@code WakefulReceiver}.
     * @param context the context of the app's Activity.
     */
    public void cancel(Context context) {
//        Log.d(LOG_TAG, "cancel()");

        // declare the broadcast Intent for this {@code WakefulReceiver}
        Intent intent = new Intent(context, WakefulReceiver.class);
        mIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // schedule the broadcast Intent received by this {@code WakefulReceiver#onReceive}
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(mIntent);

        // Disable {@code BootReceiver} so that it doesn't automatically reschedule
        // when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Snooze's the alarm by
     * @param context the context of the app's Activity.
     */
    public void snooze(Context context) {
//        Log.d(LOG_TAG, "snooze()");

        // declare the broadcast Intent for this {@code WakefulReceiver}
        Intent intent = new Intent(context, WakefulReceiver.class);
        mIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, EXTRA_SNOOZE_MINUTES);
        // recompute Calendar
        Date date = calendar.getTime();

        // schedule the broadcast Intent received by this {@code WakefulReceiver#onReceive}
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), mIntent);

        // Enable {@code BootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}