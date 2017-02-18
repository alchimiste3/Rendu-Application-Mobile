package com.android.testservice.testservice.alarm;


import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.testservice.testservice.service.MyIntentService;

import java.util.Date;

public class ControleAlarm {

    public static void start(Application application, PendingIntent pendingIntent, int interval) {
        AlarmManager manager = (AlarmManager) application.getSystemService(application.getApplicationContext().ALARM_SERVICE);

        Log.d("ControleAlarm", "start: interval = "+ interval);
        Date date = new Date();
        Log.d("ControleAlarm", "\n\nstart: Time = "+ date.getTime() + "\n\n");

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(application.getApplicationContext(), "Alarm Set", Toast.LENGTH_SHORT).show();
        Log.d("ConfActivity", "start: ");
    }

    public static void stop(Application application, PendingIntent pendingIntent) {
        AlarmManager manager = (AlarmManager) application.getSystemService(application.getApplicationContext().ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(application.getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
        Log.d("ConfActivity", "stop: ");

    }
}
