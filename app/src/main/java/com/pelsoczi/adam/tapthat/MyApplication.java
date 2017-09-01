package com.pelsoczi.adam.tapthat;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.pelsoczi.adam.tapthat.app.NfcStateReceiver;
import com.pelsoczi.adam.tapthat.app.WakefulReceiver;
import com.pelsoczi.adam.tapthat.data.AlarmDAO;
import com.pelsoczi.data.Alarm;
import com.pelsoczi.data.AlarmDatabase;

// TODO: 16-04-08 add prompt - would you like to repeat this tomorrow? if model.once
// TODO: 16-04-11 show notification when user leaves the app

public class MyApplication extends Application {

    /**
     * Reference to the global application as singleton
     * */
    private static MyApplication sInstance;

    /**
     * A {@link android.support.v4.content.WakefulBroadcastReceiver}
     * to either enable to disable scheduling of the next alarm.
     */
    private WakefulReceiver mAlarmReceiver = new WakefulReceiver();

    /**
     * A {@link android.content.BroadcastReceiver} which listens for NFC events.
     */
    private NfcStateReceiver mNfcState = new NfcStateReceiver();

    private AlarmDAO mAlarmDAO;
    private AlarmDatabase alarmDb;
    public boolean isRinging = false;
    public boolean isSnoozing = false;

    public static MyApplication getInstance() {
        return sInstance;
    }

    /**
     *  Called when the application is starting, before any other application objects
     *  have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // initialize data access object
        mAlarmDAO = new AlarmDAO();

        sInstance.alarmDb = Room.databaseBuilder(this, AlarmDatabase.class,
                AlarmDatabase.Companion.getDATABASE_NAME())
                .build();

        Alarm alarm = Alarm.Companion.getEMPTY();
    }

    /**
     * <p>Enables and disables scheduling of the next alarm by {@code WakefulReceiver}.</p>
     * <p>Called when any instance of {@code AlarmDAO} writes to the underlying singleton data
     * layer which it encapsulates. Also, called by {@code MainActivity} should the user launch
     * the app via its icon while it is snoozed.</p>
     * @param schedule <b>True</b> if there is atleast a single alarm which the user should
     *                 expect to ring. <b>False</b>, otherwise.
     */
    public void doScheduling(boolean schedule) {
        if (schedule) {
            mAlarmReceiver.set(this);
        }
        else if (!schedule) {
            mAlarmReceiver.cancel(this);
        }
    }

    /**
     * <p>Enables and disables the device's NFC via {@code NfcStateReceiver}.</p>
     * <p>Called throught the lifecycle of {@code RingingActivity}</p>
     * @param enabled <b>True</b> if the app should be aware of NFC events
     *                <b>False</b>, otherwise.
     */
    public void enableNfcStateReceiver(boolean enabled) {
        mNfcState.setEnabled(this, enabled);
    }

    /**
     * Called by {@code RingingActivity} when the user clicks snooze.
     */
    public void snooze() {
        mAlarmReceiver.snooze(this);
        isSnoozing = true;
    }

    /**
     * Called by {@code MainActivity} should the user launch the app via its icon while
     * it is snoozed.
     * @param isRinging <b>True</b>, to toggle the app's current snooze state.
     */
    public void setSnoozing(boolean isRinging) {
        this.isSnoozing = isRinging;
    }

    /**
     * Called by {@code RingingActivity} when the alarm is dismissed via NFC.
     */
    public void dismiss() {
        isRinging = false;
    }
}