package com.pelsoczi.adam.tapthat.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            Log.d("BootReceiver", "{onReceive}");

            // create a new intent to deliver to the intent service.
            Intent service = new Intent(context, AlarmManagerService.class);
            context.startService(service);
        }
    }
}