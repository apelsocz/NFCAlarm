package com.example.adam.nfcalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.scheduler.AlarmService;
import com.example.adam.nfcalarm.ui.Ringing;

public class RingingActivity extends AppCompatActivity {

    public static final String ACTION_DISMISS_ALARM = "com.example.adam.nfcalarm.ACTION_DISMISS_ALARM";
    public static final String ACTION_SNOOZE_ALARM = "com.example.adam.nfcalarm.ACTION_SNOOZE_ALARM";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInstance().setRinging(true);
        setContentView(R.layout.activity_alarm);
        String action = getIntent().getAction();

        if (action != null) {
            Log.d("RingingActivity", "onCreate()");
            if (action.equals(ACTION_SNOOZE_ALARM)) {
                snooze();
            }
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.alarmContainer, new Ringing(), Ringing.NAME)
                    .commit();
        }
    }

    public void snooze() {
        MyApplication.getInstance().snooze();
        stopRinging();
        finish();
    }

    public void dismiss() {
        ((Ringing)getSupportFragmentManager().findFragmentByTag(Ringing.NAME)).dismiss();
        MyApplication.getInstance().dismiss();

        AlarmDAO alarmDAO = new AlarmDAO();
        AlarmModel model = alarmDAO.scheduledModel();

        if (model.once) {
            model = new AlarmModel(model.uniqueID, !model.isActive, model.hour, model.minute,
                    model.once, model.sunday, model.monday, model.tuesday, model.wednesday,
                    model.thursday, model.friday, model.saturday);

        }

        alarmDAO.updateModel(model);
        stopRinging();
        finish();
    }

    public void stopRinging() {
        stopService(new Intent(this, AlarmService.class));
        Log.d("RingingActivity", "stopRinging()");
    }
}