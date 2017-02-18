package com.android.testservice.testservice.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.testservice.testservice.R;
import com.android.testservice.testservice.alarm.AlarmReceiver;
import com.android.testservice.testservice.alarm.ControleAlarm;
import com.android.testservice.testservice.write_read.WriteReadAmplitudes;


public class ConfActivity extends FragmentActivity {

    WriteReadAmplitudes writeReadAmplitudes = new WriteReadAmplitudes();

    String IPServeurString = "10.212.125.205";
    String portServeurString = "8181";

    EditText ip;
    EditText port;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    Intent alarmIntent;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf);
        Intent intent = this.getIntent();
        //Bundle bundle = intent.getExtras();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();


        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);


        if(pref.contains("ip")){
            IPServeurString = pref.getString("ip",IPServeurString);
        }
        if(pref.contains("port")){
            portServeurString = pref.getString("port",portServeurString);
        }

        ip.setText(IPServeurString);
        port.setText(portServeurString);

        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("ip",ip.getText().toString());
                editor.commit();
            }
        });

        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("port",port.getText().toString());
                editor.commit();
            }
        });
    }


    @Override
    protected void onDestroy() {


        Log.d("onDestroy", "onDestroy: ip.getText().toString() = "+ ip.getText().toString());
        Log.d("onDestroy", "onDestroy: port.getText().toString() = "+ port.getText().toString());

        super.onDestroy();
    }
}