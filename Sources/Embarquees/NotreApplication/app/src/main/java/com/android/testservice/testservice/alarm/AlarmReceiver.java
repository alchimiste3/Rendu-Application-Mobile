package com.android.testservice.testservice.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.testservice.testservice.service.MyIntentService;
import com.android.testservice.testservice.utils.CheckBattery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Date date = new Date();
        Log.d("AlarmReceiver", "onReceive: date = "+ date.getDay());
        Log.d("AlarmReceiver", "\n\nonReceive: Time = "+ date.getTime() + "\n\n");

        SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);

        String message = "";

        if (pref.contains("auRepos") && pref.contains("interieur")) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("auRepos", pref.getBoolean("auRepos", true));
                jsonObject.put("interieur", pref.getBoolean("interieur", true));
                message = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        /////////////////////////////// Test niveau batterie ///////////////////////////////

        CheckBattery checkBattery = new CheckBattery(context);

        Log.d("AlarmReceiver", "checkBattery.checkLevel() = " + checkBattery.checkLevel());
        if(checkBattery.checkLevel() < 0.20){
            Toast.makeText(context, "Niveau le batterie trop faible (< 20%).\n Enregistrement annulé", Toast.LENGTH_LONG).show();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);


        }
        else {

            /////////////////////////////// Test localisation disponnible ///////////////////////////////

            if (checkPositionActive(context)) {
                startService(context, message);
                Log.d("AlarmReceiver", "onReceive: la position est disponnible donc lancement du service");
            } else {
                Log.d("AlarmReceiver", "onReceive: position non disponnible");
            }
        }
    }


    /////////////////////////////// Gestion du service ///////////////////////////////

    public void startService(Context context, String message) {
        Intent mServiceIntent = new Intent(context, MyIntentService.class);
        mServiceIntent.setData(Uri.parse(message));
        context.startService(mServiceIntent);

    }


    public boolean checkPositionActive(Context context){
        String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            //Toast.makeText(context, "KO", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            //Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
