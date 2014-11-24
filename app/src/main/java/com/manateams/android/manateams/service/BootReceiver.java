package com.manateams.android.manateams.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.manateams.android.manateams.MainActivity;
import com.manateams.android.manateams.util.Constants;
import com.manateams.android.manateams.util.Utils;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Set alarm
            Log.d("BitBitBit", "Boot completed, setting alarms");
            Utils.setAlarms(Constants.INTERVAL_GRADE_SCRAPE, context);
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }


}
