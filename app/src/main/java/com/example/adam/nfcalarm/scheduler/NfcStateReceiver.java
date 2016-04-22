package com.example.adam.nfcalarm.scheduler;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import com.example.adam.nfcalarm.MainActivity;
import com.example.adam.nfcalarm.RingingActivity;

public class NfcStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
            int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF);
            if (state == NfcAdapter.STATE_ON) {
                Intent activity = new Intent(context, MainActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(activity);
            }
        }
    }

    public void setEnabled(Context context, boolean enabled) {
        ComponentName receiver = new ComponentName(context, NfcStateReceiver.class);
        PackageManager pm = context.getPackageManager();
        int newState = enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(receiver, newState, PackageManager.DONT_KILL_APP);
    }
}