package dev.lazyllamas.rizzyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dev.lazyllamas.rizzyclient.GPSService;

public class CloseApp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context.getApplicationContext(), GPSService.class));
       // System.exit(0);
    }
}
