package com.example.adam.nfcalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adam.nfcalarm.model.AlarmData;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.scheduler.WakefulAlarmReceiver;
import com.example.adam.nfcalarm.ui.Alarms;
import com.example.adam.nfcalarm.ui.Content;
import com.example.adam.nfcalarm.ui.Edit;

import org.json.JSONArray;
import org.json.JSONException;

public class ApplicationActivity extends AppCompatActivity {
    public static final String NAME = ApplicationActivity.class.getSimpleName();
    /**
     * Key which keeps all alarms in shared preferences
     */
    public static final String ALARM_KEY = "alarmKey";

    private WakefulAlarmReceiver alarmReceiver = new WakefulAlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragmentContainer, new Content(), Content.NAME)
                    .add(R.id.fragmentContainer, new Alarms(), Alarms.NAME)
                    .commit();
            Toast.makeText(getApplicationContext(), "savedInstance NULL", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_application, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTime(TimePicker view, int hourOfDay, int minute){
        ((Content)getSupportFragmentManager().findFragmentByTag(Content.NAME)).doTimeSet(view, hourOfDay, minute);
        Toast.makeText(getApplicationContext(), String.valueOf(view.is24HourView()), Toast.LENGTH_SHORT).show();
    }

    public void setCycle(boolean[] items){
        //update sharedpreferences - once user has hit OK
        ((Content)getSupportFragmentManager().findFragmentByTag(Content.NAME)).doFrequencySet(items);
    }

    public void setActive(boolean isSet){
        ((Content)getSupportFragmentManager().findFragmentByTag(Content.NAME)).doSchedule(isSet);
    }

    public void onAlarmClick(AlarmModel model) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // remove previous instance of edit
        fragmentManager.popBackStackImmediate(Edit.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // add new instance
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, Edit.newInstance(model), Edit.NAME)
                .addToBackStack(Edit.NAME)
                .commit();
    }

    public void onEditUpdate() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.popBackStackImmediate(Edit.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Alarms alarms = (Alarms) fragmentManager.findFragmentByTag(Alarms.NAME);
        if (alarms != null) {
            alarms.update();
        }
    }

    public void onActiveToggle(AlarmModel model, int position) {
        Alarms alarms = (Alarms) getSupportFragmentManager().findFragmentByTag(Alarms.NAME);
        if (alarms != null) {
            alarms.toggleAlarm(model, position);
        }
    }

    public void doAlarmSchedule(boolean scheduleAlarm) {
        if (scheduleAlarm) {
            alarmReceiver.setAlarm(this);
        }
        else if (!scheduleAlarm) {
            alarmReceiver.cancelAlarm(this);
        }
    }
}
