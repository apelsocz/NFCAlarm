package com.example.adam.nfcalarm;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.ui.Alarms;
import com.example.adam.nfcalarm.ui.Edit;

import org.json.JSONArray;

public class AlarmsActivity extends AppCompatActivity {
    //// TODO: 16-03-13 App needs to be aware of shifts in time
    // - change in longitude / latitude
    // - manually update timezone
    // - leap years
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
        mAlarmDAO.setModels(json);
    }
}