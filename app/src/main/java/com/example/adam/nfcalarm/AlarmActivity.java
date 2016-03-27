package com.example.adam.nfcalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.adam.nfcalarm.data.AlarmDataManager;
import com.example.adam.nfcalarm.scheduler.AlarmService;
import com.example.adam.nfcalarm.ui.Display;

public class AlarmActivity extends AppCompatActivity {

    public static final String ACTION_DISMISS_ALARM = "com.example.adam.nfcalarm.ACTION_DISMISS_ALARM";
    public static final String ACTION_SNOOZE_ALARM = "com.example.adam.nfcalarm.ACTION_SNOOZE_ALARM";

    private BroadcastReceiver mAlarmActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("AlarmActivity", "mAlarmActionReceiver:onReceive()");
            if (intent.getAction().equals(ACTION_DISMISS_ALARM)) {
                stopRinging();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmDataManager.initializeInstance(getApplicationContext());

        setContentView(R.layout.activity_alarm);
        String action = getIntent().getAction();

        if (action != null) {
            Log.d("AlarmActivity", "onCreate()");
            if (action.equals(ACTION_DISMISS_ALARM)) {
                stopRinging();
            }
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.alarmContainer, new Display(), Display.NAME)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        registerReceiver(mAlarmActionReceiver, new IntentFilter(ACTION_DISMISS_ALARM));
    }

    public void stopRinging() {
        Intent mpIntent = new Intent(this, AlarmService.class);
        mpIntent.setAction(AlarmService.ACTION_PLAY);
        stopService(mpIntent);
        Log.d("ApplicationActivity", "stopRinging()");
    }


}