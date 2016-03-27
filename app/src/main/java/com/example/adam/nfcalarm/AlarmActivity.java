package com.example.adam.nfcalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.adam.nfcalarm.scheduler.AlarmService;
import com.example.adam.nfcalarm.ui.Display;

public class AlarmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.alarmContainer, new Display(), Display.NAME)
                    .commit();
        }
    }

    public void stopRinging(){
        Intent mpIntent = new Intent(this, AlarmService.class);
        mpIntent.setAction(AlarmService.ACTION_PLAY);
        stopService(mpIntent);
        Log.d("ApplicationActivity", "stopRinging()");
    }
}