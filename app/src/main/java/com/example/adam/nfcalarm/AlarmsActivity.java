package com.example.adam.nfcalarm;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;

import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.ui.Alarms;
import com.example.adam.nfcalarm.ui.Edit;

import java.util.List;


public class AlarmsActivity extends AppCompatActivity {

    public static final String NAME = AlarmsActivity.class.getSimpleName();
    private FloatingActionButton mFAB;
    private AlarmDAO mAlarmDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        mFAB = (FloatingActionButton) findViewById(R.id.floating_action_btn);
        setSupportActionBar(toolbar);
        mAlarmDAO = new AlarmDAO();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new Alarms(), Alarms.NAME)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFAB.show();
    }

    // Called from Alarms.java
    public void onAlarmClick(AlarmModel model) {
        mFAB.hide();

        FragmentManager fragmentManager = getSupportFragmentManager();
        // remove previous instance of edit
        fragmentManager.popBackStackImmediate(Edit.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        // add new instance
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragmentContainer, Edit.newInstance(model), Edit.NAME)
                .addToBackStack(Edit.NAME)
                .commit();
    }

    public void onEditUpdate() {
        mFAB.show();

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