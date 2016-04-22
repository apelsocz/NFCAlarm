package com.example.adam.nfcalarm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.scheduler.AlarmService;
import com.example.adam.nfcalarm.ui.Ringing;

public class RingingActivity extends AppCompatActivity {

    public static final String ACTION_DISMISS_ALARM = "com.example.adam.nfcalarm.ACTION_DISMISS_ALARM";
    public static final String ACTION_SNOOZE_ALARM = "com.example.adam.nfcalarm.ACTION_SNOOZE_ALARM";

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInstance().isRinging = true;
        setContentView(R.layout.activity_alarm);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.alarmContainer, new Ringing(), Ringing.NAME)
                    .commit();
        }
        else {
            Log.d("RingingActivity", "onCreate(savedinstance not null)");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String action = getIntent().getAction();
        if (action != null) {
            Log.d("RingingActivity", "onCreate(" + action + ")");
            if (action.equals(ACTION_SNOOZE_ALARM)) {
                snooze();
            }
            if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
                    action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
                dismiss();
            }
        }

        if (!mNfcAdapter.isEnabled()) {
            MyApplication.getInstance().setmNfcStateReceiver(true);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.alarmContainer),
                    "Near Field Communication is OFF", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                        }
                    });
            snackbar.getView().setBackgroundColor(Color.parseColor("#212121"));
            snackbar.show();

        }
        else {
            MyApplication.getInstance().setmNfcStateReceiver(false);
        }

        /**
         * It's important, that the activity is in the foreground.
         * Otherwise an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void snooze() {
        MyApplication.getInstance().snooze();
        MyApplication.getInstance().setmNfcStateReceiver(false);
        stopRinging();
        finishAffinity();
    }

    public void dismiss() {

        AlarmDAO alarmDAO = new AlarmDAO();
        AlarmModel model = alarmDAO.scheduledModel();

        if (model.once) {
            model = new AlarmModel(model.uniqueID, !model.isActive, model.hour, model.minute,
                    model.once, model.sunday, model.monday, model.tuesday, model.wednesday,
                    model.thursday, model.friday, model.saturday);
        }

        alarmDAO.updateModel(model);
        stopRinging();
        MyApplication.getInstance().dismiss();
        finishAffinity();
    }

    public void stopRinging() {
        stopService(new Intent(this, AlarmService.class));
        Log.d("RingingActivity", "stopRinging()");
    }

    public static void setupForegroundDispatch(Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(
                activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[2];
        String[][] techList = new String[][]{};

        // same filter as manifest - action, category
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filters[1] = new IntentFilter();
        filters[1].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}