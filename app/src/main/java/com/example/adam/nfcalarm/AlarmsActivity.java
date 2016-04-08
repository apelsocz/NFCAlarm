package com.example.adam.nfcalarm;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.data.AlarmDataManager;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.ui.Alarms;
import com.example.adam.nfcalarm.ui.Edit;

import org.json.JSONArray;

public class AlarmsActivity extends AppCompatActivity {
    //// TODO: 16-03-13 App needs to be aware of shifts in time
    // - change in longitude / latitude
    // - manually update timezone
    // - leap years
    // - leap seconds
    // - day light savings time

    public static final String NAME = AlarmsActivity.class.getSimpleName();
    private AlarmDAO mAlarmDAO;

/*
    private WakefulAlarmReceiver alarmReceiver = new WakefulAlarmReceiver();
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        mAlarmDAO = new AlarmDAO();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new Alarms(), Alarms.NAME)
                    .commit();
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

    // Called from Alarms.java
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

        // remove previous instance of edit
        fragmentManager.popBackStackImmediate(Edit.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // add new instance
        Alarms alarms = (Alarms) fragmentManager.findFragmentByTag(Alarms.NAME);
        if (alarms != null) {
            alarms.update();
        }
    }

    public void onActiveToggle(AlarmModel model) {
        Alarms alarms = (Alarms) getSupportFragmentManager().findFragmentByTag(Alarms.NAME);
        if (alarms != null) {
            alarms.toggleAlarm(model);
        }
    }

    public void doAlarmsUpdate(JSONArray json) {
//        AlarmDataManager.getInstance().updateAlarmsData(json);
        mAlarmDAO.addModels(json);
    }

/*    public void doScheduling(boolean scheduleAlarm) {
        if (scheduleAlarm) {
            alarmReceiver.setAlarm(this);
        }
        else if (!scheduleAlarm) {
            alarmReceiver.cancelAlarm(this);
        }
    }*/
}