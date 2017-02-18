package com.android.testservice.testservice.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by user on 29/11/16.
 */

public class CheckBattery {


    Intent batteryStatus;

    public CheckBattery(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = context.registerReceiver(null, ifilter);
    }

    public float checkLevel() {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        return batteryPct;
    }


}
