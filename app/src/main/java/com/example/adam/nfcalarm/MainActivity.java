package com.example.adam.nfcalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.adam.nfcalarm.scheduler.AlarmService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate()");

        boolean isRinging = MyApplication.getInstance().getRinging();
        boolean isSnoozed = MyApplication.getInstance().getSnoozing();

        if (isRinging || isSnoozed) {
            if (isSnoozed) {
                Context context = getApplicationContext();
                Intent mpIntent = new Intent(context, AlarmService.class);
                mpIntent.setAction(AlarmService.ACTION_PLAY);
                context.startService(mpIntent);
                MyApplication.getInstance().setSnoozing(false);
            }
            startActivity(new Intent(getApplicationContext(), RingingActivity.class));
        }
        else {
            startActivity(new Intent(getApplicationContext(), AlarmsActivity.class));
        }
        finish();
    }
}