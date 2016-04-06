package com.example.adam.nfcalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.adam.nfcalarm.data.AlarmDataManager;
import com.example.adam.nfcalarm.scheduler.AlarmService;
import com.example.adam.nfcalarm.ui.Display;

public class RingingActivity extends AppCompatActivity {

    public static final String ACTION_DISMISS_ALARM = "com.example.adam.nfcalarm.ACTION_DISMISS_ALARM";
    public static final String ACTION_SNOOZE_ALARM = "com.example.adam.nfcalarm.ACTION_SNOOZE_ALARM";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInstance().setAlarmRinging(true);
        setContentView(R.layout.activity_alarm);
        String action = getIntent().getAction();

        if (action != null) {
            Log.d("RingingActivity", "onCreate()");
            if (action.equals(ACTION_SNOOZE_ALARM)) {
                snooze();
            }
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.alarmContainer, new Display(), Display.NAME)
                    .commit();
        }
    }

    public void snooze() {
        MyApplication.getInstance().snooze();
        stopRinging();
    }

    public void dismiss() {
        AlarmDataManager alarmDataManager = AlarmDataManager.getInstance();
        long id = alarmDataManager.getNextAlarmID();
        alarmDataManager.doAlarmDismissed(id);
    }

    public void stopRinging() {
        stopService(new Intent(this, AlarmService.class));
        Log.d("RingingActivity", "stopRinging()");
    }
}