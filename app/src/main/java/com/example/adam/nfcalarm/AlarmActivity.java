package com.example.adam.nfcalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.adam.nfcalarm.ui.PlaceholderFragment;

import net.danlew.android.joda.JodaTimeAndroid;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        setContentView(R.layout.activity_alarm);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.alarmContainer, new PlaceholderFragment()).commit();
        }

        Intent intent = getIntent();

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
