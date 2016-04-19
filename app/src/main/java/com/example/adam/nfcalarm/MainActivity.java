package com.example.adam.nfcalarm;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
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

        boolean isRinging = MyApplication.getInstance().isRinging;
        boolean isSnoozed = MyApplication.getInstance().isSnoozing;

        if (isRinging || isSnoozed) {
            Intent activity = new Intent(getApplicationContext(), RingingActivity.class);
            if (isSnoozed) {
                Context context = getApplicationContext();
                Intent media = new Intent(context, AlarmService.class);
                media.setAction(AlarmService.ACTION_PLAY);

                context.startService(media);
                MyApplication.getInstance().setSnoozing(false);
                MyApplication.getInstance().doScheduling(false);
            }
            startActivity(activity);
        }
        else {
            startActivity(new Intent(getApplicationContext(), AlarmsActivity.class));
        }
        finish();
    }
}