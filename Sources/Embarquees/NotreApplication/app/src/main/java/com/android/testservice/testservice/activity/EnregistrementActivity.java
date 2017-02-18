package com.android.testservice.testservice.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.testservice.testservice.R;
import com.android.testservice.testservice.alarm.AlarmReceiver;
import com.android.testservice.testservice.alarm.ControleAlarm;
import com.android.testservice.testservice.write_read.WriteReadAmplitudes;


public class EnregistrementActivity extends FragmentActivity {

    WriteReadAmplitudes writeReadAmplitudes = new WriteReadAmplitudes();

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Intent alarmIntent;
    PendingIntent pendingIntent;

    Button startAlarm;
    TextView intervalTextView;
    int interval = 1;

    Button cleanFileButton;
    Dialog dialogClean;

    Switch switchActivite;

    Switch switchInterieur;

    Button stopAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement);
        final Intent intent = this.getIntent();
        //Bundle bundle = intent.getExtras();

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();




        ////////////////////////////// Les Switchs //////////////////////////////////////


        switchInterieur = (Switch) findViewById(R.id.interieur);

        if (pref.contains("interieur")) {
            switchInterieur.setChecked(pref.getBoolean("interieur", true));

            if (switchInterieur.isChecked()) {
                switchInterieur.setText("A l'interieur");
            } else {
                switchInterieur.setText("A l'exterieur");
            }
        }

        switchInterieur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Switch", "onCheckedChanged: isChecked = "+isChecked);

                editor.putBoolean("interieur",isChecked);
                editor.commit();

                try {
                    if (isChecked) {
                        switchInterieur.setText("A l'interieur");
                    } else {
                        switchInterieur.setText("A l'exterieur");
                    }
                }
                catch (Exception e){

                }

            }
        });


        switchActivite= (Switch) findViewById(R.id.activite);

        if (pref.contains("auRepos")) {
            switchActivite.setChecked(!pref.getBoolean("auRepos", false));

            if (switchActivite.isChecked()) {
                switchActivite.setText("Oui");
            } else {
                switchActivite.setText("Non");
            }
        }

        switchActivite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Switch", "onCheckedChanged: isChecked = "+isChecked);

                editor.putBoolean("auRepos",!isChecked);
                editor.commit();


                try {
                    if (isChecked) {
                        switchActivite.setText("Oui");
                    } else {
                        switchActivite.setText("Non");
                    }
                }
                catch (Exception e){

                }

            }
        });


        ///////////////////////////////// Interval ////////////////////////////////////

        intervalTextView = (TextView) findViewById(R.id.interval);
        SeekBar seekBarInterval = (SeekBar) findViewById(R.id.seekBarInterval);

        if(pref.contains("interval") ){
            try {
                interval = pref.getInt("interval", 5);
                intervalTextView.setText(String.valueOf(interval) + " min");
                seekBarInterval.setProgress(interval - 1);


            }
            catch (Exception e){
            }
        }


        seekBarInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                interval = (i + 1);
                intervalTextView.setText(interval + " min");

                editor.putInt("interval",interval);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        ///////////////////////////////// AlarmReceiver ////////////////////////////////////

        alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        startAlarm = (Button) findViewById(R.id.startAlarm);

        startAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("ConfActivity", "interval = "+interval);



                if (checkPositionActive()) {

                    ControleAlarm.start(getApplication(), pendingIntent, interval * 1000 * 60);

                }


            }
        });

        stopAlarm = (Button) findViewById(R.id.stopAlarm);

        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControleAlarm.stop(getApplication(), pendingIntent);

            }
        });

        //////////////////////// Clean ////////////////////////


        cleanFileButton = (Button) findViewById(R.id.cleanFileButton);
        cleanFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClean.show();
            }
        });



        //////////////////////// Boite de dialogue (clean) ////////////////////////

        dialogClean = new Dialog(this);
        dialogClean.setContentView(R.layout.popup_cancel_ok);
        dialogClean.setTitle("Suppression de fichiers : ");

        final Button dialogCancelKO = (Button) dialogClean.findViewById(R.id.dialogCancelKO);
        final Button dialogCancelOK = (Button) dialogClean.findViewById(R.id.dialogCancelOK);
        dialogCancelKO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClean.dismiss();

            }
        });
        dialogCancelOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReadAmplitudes.cleanFile();
                dialogClean.dismiss();
            }
        });




    }

    public boolean checkPositionActive(){
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            Toast.makeText(getApplicationContext(), "Activer la localisation sur votre smart phone", Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            return true;

        }


    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}