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

        Intent intent = getIntent();

        boolean isRinging = MyApplication.getInstance().getRinging();
        boolean isSnoozed = MyApplication.getInstance().getSnoozing();

        if (isRinging || isSnoozed) {
            Intent activity = new Intent(getApplicationContext(), RingingActivity.class);
//            if (intent != null) {
//                String action = intent.getAction();
//                if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
//                        action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
//                    activity.setAction(RingingActivity.ACTION_DISMISS_ALARM);
//                }
//            }
            if (isSnoozed) {
                Context context = getApplicationContext();
                Intent mpIntent = new Intent(context, AlarmService.class);
                mpIntent.setAction(AlarmService.ACTION_PLAY);
                context.startService(mpIntent);
                MyApplication.getInstance().setSnoozing(false);
            }
            startActivity(activity);
        }
        else {
            startActivity(new Intent(getApplicationContext(), AlarmsActivity.class));
        }
        finish();
    }
}