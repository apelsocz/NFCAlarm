package com.example.adam.nfcalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.adam.nfcalarm.data.AlarmDAO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate()");

        boolean isRinging = MyApplication.getInstance().getAlarmRinging();

        if (isRinging) {
            startActivity(new Intent(getApplicationContext(), RingingActivity.class));
        }
        else {
            startActivity(new Intent(getApplicationContext(), AlarmsActivity.class));
        }
        finish();
    }
}