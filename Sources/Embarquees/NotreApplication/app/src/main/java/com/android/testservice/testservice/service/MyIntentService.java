package com.android.testservice.testservice.service;

import android.Manifest;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.testservice.testservice.utils.CheckPermissions;
import com.android.testservice.testservice.utils.ClientREST;
import com.android.testservice.testservice.utils.Recorder;
import com.android.testservice.testservice.write_read.WriteReadAmplitudes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 29/11/16.
 */

public class MyIntentService extends IntentService {

    WriteReadAmplitudes writeReadAmplitudes = new WriteReadAmplitudes();

    private static int TEMPS_DE_CAPTURE = 500; // NE PAS METTRE UNE VALEUR PLUS PETITE : le micro prend un certain temps pour capture des donnees
    private static int TEMPS_ENTRE_CHAQUE_ECHANTILLON = 20; //
    private static int TEMPS_ENTRE_CHAQUE_CAPTURE = 30000; //TODO 30s pour l'instant mais normalement c'est une heure

    private static long UNE_HEURE = 3600000;

    ///////////////////////////////// RECORDER VAR /////////////////////////////////
    Recorder recorder;
    ArrayList<Double> listAmpl = new ArrayList<>();

    ///////////////////////////////// LOCATION VAR /////////////////////////////////
    LocationManager locationManager;
    LocationListener locationListener;
    private double latitude = 0.0;
    private double longitude = 0.0;


    ///////////////////////////////// LABEL VAR /////////////////////////////////

    private boolean auRepos = true;
    private boolean interieur = true;

    ///////////////////////////////// CONSTRUCTEUR /////////////////////////////////
    public MyIntentService() {
        super("MyIntentService");
    }

    public MyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        recorder = new Recorder(getApplicationContext(), mFileName);

        // On lance  la capture des donnees dans une thread
        new Thread(runCapture).start();

        try {
            Log.d("onHandleIntent", "salut dans indent");

            for(int i = 0 ; i < 5 ; i++) {
                listAmpl = new ArrayList<>();

                // On lance l'enregistrement, ce qui permet a la thread de capturer les donnees
                recorder.recorder(); // Start

                // On attend le temps de la capture
                Thread.sleep(TEMPS_DE_CAPTURE);

                // On stope l'enregistrement
                recorder.recorder(); // Stop

                // Si on a pu get la localisation, on enregistrer la donnee.
                if (latitude != 0 && longitude != 0) {
                    Date date = new Date(); // GMT 0
                    date.setTime(date.getTime() + UNE_HEURE); // GMT 1
                    writeReadAmplitudes.addAmplitude(calculeMoyenneAmplitude(), latitude, longitude, auRepos, interieur, date);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private double calculeMoyenneAmplitude(){

        writeReadAmplitudes.writeData("\ncalculeMoyenneAmplitude size list = "+ listAmpl.size(), true);

        Double res = 0.0;

        for(Double ampl : listAmpl) {
            writeReadAmplitudes.writeData("\ncalculeMoyenneAmplitude " + ampl, true);
            res += ampl;
        }

        return res/((double)listAmpl.size());
    }

    ////////////////////////////////// Capture ansynchrone des donnees //////////////////////////////////

    Runnable runCapture = new Runnable() {

        public void run() {
            while (true) {

                try {


                    if (recorder.getmRecorder() != null) {


                        try {

                            final int amp = recorder.getmRecorder().getMaxAmplitude();

                           // writeReadAmplitudes.writeData("\n getAudioSourceMax = " + recorder.getmRecorder().getAudioSourceMax(), true);

                            final double ampDb = 20 * Math.log(amp) / Math.log(10);

                            if (ampDb >= 0) {
                                //Date date = new Date();
                                //writeReadAmplitudes.addAmplitude(ampDb, latitude, longitude, auRepos, interieur, date);
                                listAmpl.add(ampDb);
                            }



                        } catch (Exception e) {
                            Log.d("Erreur", "Erreur = " + e.toString());
                        }

                        Thread.sleep((long) TEMPS_ENTRE_CHAQUE_ECHANTILLON);

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };



    ///////////////////////////////// LOCATION /////////////////////////////////

    public class LocaListener implements android.location.LocationListener {

        public LocaListener(String provider) {
            writeReadAmplitudes.writeData("\nLocation " + provider, true);
        }

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (latitude == 0 || longitude == 0) {
                if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            // writeReadAmplitudes.writeData("\nMyIntentService nouvelle localisation = " + latitude + "," + longitude, true);

            Log.d("MyIntentService", "\n\n\n\n\nLatitude : " + location.getLatitude() + " et Longitude : " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void location() {
        boolean perm = CheckPermissions.checkPermissionLocation(getApplicationContext());
        writeReadAmplitudes.writeData("\nMyIntentService Location perm = " + perm, true);

        if (perm) {
            writeReadAmplitudes.writeData("\nMyIntentService Location", true);

            locationManager = (LocationManager) getApplication().getSystemService(getApplicationContext().LOCATION_SERVICE);

            /**
             * Obliger de mettre cette verification pour la compilation
             */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //CheckPermissions.checkPermissionLocation(getApplicationContext());
                writeReadAmplitudes.writeData("\n\n\nMyIntentService checkSelfPermission false", true);

                Location location = locationManager.getLastKnownLocation(getApplicationContext().LOCATION_SERVICE);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }
            else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocaListener(LocationManager.NETWORK_PROVIDER));
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocaListener(LocationManager.GPS_PROVIDER));
            }
        }
        else {
            Log.d("Perm location","Perm = "+perm);

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int rep = super.onStartCommand(intent, flags, startId);

        if(intent != null) {
            try {
                String message = intent.getDataString();
                writeReadAmplitudes.writeData("\nMyIntentService message = "+ message, true);

                JSONObject jsonObject = new JSONObject(message);
                auRepos = jsonObject.getBoolean("auRepos");
                interieur = jsonObject.getBoolean("interieur");
                writeReadAmplitudes.writeData("\nMyIntentService auRepos = "+ auRepos, true);
                writeReadAmplitudes.writeData("\nMyIntentService interieur = "+ interieur, true);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Utilisation de locationManager
        location();

        return rep;//START_STICKY;
    }

    @Override
    public ComponentName startService(Intent service) {
        Log.d("MyIntentService", "startService");
        writeReadAmplitudes.writeData("\nMyIntentService startService", true);
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("MyIntentService", "stopService");
        writeReadAmplitudes.writeData("\nMyIntentService stopService", true);
        return super.stopService(name);
    }

    @Override
    public void onCreate() {
        Log.d("MyIntentService", "onCreate");
        writeReadAmplitudes.writeData("\nMyIntentService onCreate", true);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("MyIntentService", "onDestroy");
        writeReadAmplitudes.writeData("\nMyIntentService onDestroy", true);
        super.onDestroy();

    }

}
