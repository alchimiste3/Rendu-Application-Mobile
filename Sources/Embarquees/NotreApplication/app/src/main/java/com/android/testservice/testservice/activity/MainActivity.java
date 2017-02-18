package com.android.testservice.testservice.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.testservice.testservice.R;
import com.android.testservice.testservice.alarm.AlarmReceiver;
import com.android.testservice.testservice.business.ListeFiltres;
import com.android.testservice.testservice.service.MyIntentService;
import com.android.testservice.testservice.utils.ClientREST;
import com.android.testservice.testservice.business.Cluster;
import com.android.testservice.testservice.business.Day;
import com.android.testservice.testservice.utils.JSONTrans;
import com.android.testservice.testservice.write_read.NameFiles;
import com.android.testservice.testservice.write_read.WriteReadAmplitudes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    //////////////////////////////////// Gestion Service ////////////////////////////////////

    Messenger mService = null;
    boolean mBound;

    String IPServeurString = "10.212.125.205";
    String portServeurString = "8181";

    //////////////////////////////////// Gestion BroadcastReceiver ////////////////////////////////////

    Intent alarmIntent;
    PendingIntent pendingIntent;
    int interval = 10000;

    //////////////////////////////////// Gestion Ecriture/Lecture fichier ////////////////////////////////////

    WriteReadAmplitudes writeReadAmplitudes = new WriteReadAmplitudes();

    //////////////////////////////////// Gestion PopUp ////////////////////////////////////

    Dialog dialogSend;

    //////////////////////////////////// Gestion preferences utilisateur ////////////////////////////////////

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    int nbClustersInit = 3, nbClusters;
    ListeFiltres listeFiltres = new ListeFiltres();
    boolean clusterFile = false;


    //////////////////////////////////// Boutons ////////////////////////////////////

    /*ProgressBar progressBar;
    TextView textChargement;*/

    Switch switchAuRepos;
    Switch switchInterieur;

    Spinner spinner;

    //////////////////////////////////// Bouton position smart Phone ////////////////////////////////////

    boolean positionButtonOk = false;

    //////////////////////////////////// Permission de l'app ////////////////////////////////////

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On charge la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // verification et validation de toute les permissions pour l'app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(LOCATION_PERMS, 15);
        }

        // Recuperation de la liste de toutes les preferences utilisateur
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();


        /////////////////////// Recupération des preferences utilisateur ///////////////////////

        if (pref.contains("ip") && pref.contains("port")) {
            IPServeurString = pref.getString("ip", null);
            portServeurString = pref.getString("port", null);
        }

        if (pref.contains("interval")) {
            interval = pref.getInt("interval", 10000);
        }



        // On verifie et valide les permissions une nouvelle fois
        checkAndSetAllPermissions(getApplicationContext());


        // On verifie si la position est activer sur le téléphone, si on peur get la position.
        // Pour ne pas avoir (0,0)
        checkPositionActive();


        /*
            Ici, on lance un BroadcastReceiver qui va lancer notre service tout les n temps environ.
            Le service capture quelque donnée puis s'arrete.
         */
        alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        /*
        // Si la localisation est active
        if (positionButtonOk){
            ControleAlarm.start(getApplication(), pendingIntent, interval);
            Log.d("onCreate", " interval =  " + interval);
        }
        else{
            // On demande a l'utilisateur de l'active
            Toast.makeText(getApplicationContext(), "Position desactive (capture sonore non lancer)\nRelancer l'application avec le positionnement activé", Toast.LENGTH_LONG).show();
        }

        */


        // Pour choisir le jour de la semaine que l'on veut check
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Spinner", "onItemClick: position = "+position);
                Log.d("Spinner", "onItemClick: id = "+id);
                listeFiltres.setJour(Day.getDay(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Repos ou en activite
        switchAuRepos = (Switch) findViewById(R.id.auRepos);
        switchAuRepos.setChecked(!listeFiltres.isAuRepos());
        switchAuRepos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Switch", "onCheckedChanged: isChecked = "+isChecked);

                listeFiltres.setAuRepos(!isChecked);
                editor.putBoolean("auRepos",!isChecked);
                editor.commit();

                try {
                    if (isChecked) {
                        switchAuRepos.setText("En activite");
                    } else {
                        switchAuRepos.setText("Au repos");
                    }
                }
                catch (Exception e){

                }

            }
        });



        // En exterieur ou en interieur
        switchInterieur = (Switch) findViewById(R.id.interieur);
        switchInterieur.setChecked(listeFiltres.isInterieur());
        switchInterieur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Switch", "onCheckedChanged: isChecked = "+isChecked);
                listeFiltres.setInterieur(isChecked);

                editor.putBoolean("interieur",isChecked);
                editor.commit();

                try {
                    if (isChecked) {
                        switchInterieur.setText("En interieur");
                    } else {
                        switchInterieur.setText("En exterieur");
                    }
                }
                catch (Exception e){

                }

            }
        });


        ///////////////////////////////// Precision - nombre de clusters ///////////////////

        SeekBar nbClustersBar = (SeekBar) findViewById(R.id.seek1);

        if(pref.contains("nbClusters") ){
            nbClusters = pref.getInt("nbClusters",nbClustersInit);
        }

        nbClustersBar.setProgress(nbClusters);

        nbClustersBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                i = i + 3;
                nbClusters = i;
                Log.d("nbClustersBar", "nbClusters = "+ nbClusters);
                editor.putInt("nbClusters",i);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ///////////////////////////////// Afficher map ///////////////////

/*        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textChargement = (TextView) findViewById(R.id.textChargement);

        progressBar.setVisibility(View.INVISIBLE);
        textChargement.setVisibility(View.INVISIBLE);*/

        // Creation des cluster, de la map et affichage
        final Button buttonAfficherCarte = (Button) findViewById(R.id.buttonAfficherCarte);
        buttonAfficherCarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*progressBar.setVisibility(View.VISIBLE);
                textChargement.setVisibility(View.VISIBLE);*/

                clusterFile = false;
                getClustersJSONFromService(); // TODO gestion de ansynchronisme

                Date dateDebut = new Date();
                int timeOut = 10000;
                // Boucle de synchro
                while (!clusterFile){
                    Date dateCourante = new Date();
                    if(dateCourante.getTime() > dateDebut.getTime() + timeOut){ // On a depasse les timeout
                        break;
                    }
                }

                // Si le serveur a repondu
                if(clusterFile) {
                    try {
                        String jsonTest = writeReadAmplitudes.readData(NameFiles.CLUSTER_FILE);

                        JSONObject jsonObject = new JSONObject(jsonTest);

                        Log.d("jsonObject", "jsonObject: jsonObject.has(\"error\") = " + jsonObject.has("error"));

                        if (jsonObject.has("error")) {
                            Toast.makeText(getApplicationContext(), "Donnees insuffisantes sur le serveur pour ces parametres", Toast.LENGTH_LONG).show();
                            /*progressBar.setVisibility(View.INVISIBLE);
                            textChargement.setVisibility(View.INVISIBLE);*/

                        } else {
                            ////// TODO Pour les test
                            //String jsonTest = getString(R.string.stringTestJson);

                            ArrayList<Cluster> listeCluster = (ArrayList<Cluster>) JSONTrans.getListCluster(jsonTest);

                            Log.d("listeCluster", "\n\n\nlisteCluster = " + listeCluster.toString());

                            Intent intent = new Intent(getApplicationContext(), MapActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("clusters", listeCluster);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Le serveur ne répond pas !!", Toast.LENGTH_LONG).show();

                }

            }
        });


        final Button enregistrementButton = (Button) findViewById(R.id.enregistrementButton);
        enregistrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EnregistrementActivity.class);
                startActivity(intent);
            }
        });




        //////////////////////// Boite de dialogue (sendAmplitudes) ////////////////////////

        // Permet la validation de l'utilisateur avant l'envoie de donnees recolte au serveur

        dialogSend = new Dialog(this);
        dialogSend.setContentView(R.layout.popup_send_ok);
        dialogSend.setTitle("Serveur : ");

        final Button dialogSendKO = (Button) dialogSend.findViewById(R.id.dialogSendKO);
        final Button dialogSendOK = (Button) dialogSend.findViewById(R.id.dialogSendOK);
        dialogSendKO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSend.dismiss();

            }
        });
        dialogSendOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAmplitudeJSONToService();
                dialogSend.dismiss();
            }
        });

    }

    /////////////////////////////// Gestion des permissions ///////////////////////////////


    /**
     * Permet de check et set toutes les permissions de l'appli une bonne fois pour toute
     * @param context
     */
    public void checkAndSetAllPermissions(final Context context) {

        // On verifie chaque permissions une a une
        boolean fineLoc = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean write = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean record = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        boolean read = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean inter = ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;

        Log.d("checkPermission","\nfineLoc = "+fineLoc+"\nwrite = "+write+"\nrecord = "+record+"\nread = "+read+"\ninter = "+inter+"\n");

        // Si l'une de permissions n'est pas valide
        if(!(fineLoc && write && record && read && inter)){
            Log.d("checkPermission", "\nperm == false");

            // Demande de validation pour chaque permissio
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("RequestPermissions", "\nrequestPermissions");
                requestPermissions(LOCATION_PERMS, 15);
            }

        }

    }

    /**
     * Permet de verifier que la localistion est bien active sur le tel
     */
    public void checkPositionActive(){
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            Toast.makeText(getApplicationContext(), "Activer la localisation sur votre smart phone", Toast.LENGTH_LONG).show();
            positionButtonOk = false;
        }
        else{
            //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            positionButtonOk = true;
        }
    }


    /////////////////////////////// GET et POST sur le service ///////////////////////////////

    private void sendAmplitudeJSONToService() {

        final JSONObject jsonObject = writeReadAmplitudes.getAmplitudesJSON();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    writeReadAmplitudes.writeData("\nMyIntentService ClientSOAP", true);

                    ClientREST ClientREST = new ClientREST(jsonObject);
                    ClientREST.setIP(IPServeurString);
                    ClientREST.setPort(portServeurString);
                    ClientREST.sendAmplitudes();


                } catch (Exception e) {
                    writeReadAmplitudes.writeData("\nMyIntentService ClientSOAP erreur", true);
                    e.printStackTrace();
                }

                //TODO decommente pour la version final :
                // // Permet de vide le fichier une fois le amplitude envoyer
                // writeReadAmplitudes.cleanFile(NameFiles.AMPLITUDE_FILE.toString());
            }
        });
        thread.start();
    }

    private void getClustersJSONFromService() {


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    writeReadAmplitudes.writeData("\nMyIntentService ClientSOAP", true);

                    ClientREST ClientREST = new ClientREST();
                    ClientREST.setIP(IPServeurString);
                    ClientREST.setPort(portServeurString);

                    String res;

                    if(!listeFiltres.getJour().equals("null")){
                        res = ClientREST.getClusters(listeFiltres.isAuRepos(), listeFiltres.isInterieur(), listeFiltres.getJour(),nbClusters);
                    }
                    else{
                        res = ClientREST.getClusters(listeFiltres.isAuRepos(), listeFiltres.isInterieur(), nbClusters);
                    }



                    writeReadAmplitudes.writeData(res, false, NameFiles.CLUSTER_FILE.toString());

                    clusterFile = true;

                } catch (Exception e) {
                    writeReadAmplitudes.writeData("\nMyIntentService ClientSOAP erreur", true);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    //////////////////////////////// Gestion du Menu ////////////////////////////////

    // Gestion des actions sur le menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        WriteReadAmplitudes writeReadAmplitudes = new WriteReadAmplitudes();

        switch (id) {

            case R.id.sendData :
                dialogSend.show();
                return true;


            case R.id.getClusters :
                getClustersJSONFromService();
                return true;

            case R.id.option :
                Intent intent = new Intent(getApplicationContext(), ConfActivity.class);
                startActivity(intent);
                return true;





        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        Log.d("onStart", "Main onStart: ");

        IPServeurString = pref.getString("ip", IPServeurString);
        portServeurString = pref.getString("port", portServeurString);
        interval = pref.getInt("interval",interval);
        listeFiltres.setAuRepos(pref.getBoolean("auRepos", true));
        listeFiltres.setInterieur(pref.getBoolean("interieur", true));


        Log.d("onStart", "Main onStart: IPServeurString = "+IPServeurString);
        Log.d("onStart", "Main onStart: portServeurString = "+portServeurString);
        Log.d("onStart", "Main onStart: interval = "+interval);
        Log.d("onStart", "Main onStart: auRepos = "+listeFiltres.isAuRepos());
        Log.d("onStart", "Main onStart: interieur = "+listeFiltres.isInterieur());

        switchAuRepos.setChecked(!listeFiltres.isAuRepos());
        switchInterieur.setChecked(listeFiltres.isInterieur());

        /*progressBar.setVisibility(View.INVISIBLE);
        textChargement.setVisibility(View.INVISIBLE);*/

        super.onStart();
    }

    @Override
    protected void onResume() {

        Log.d("onResume", "Main onResume: ");
        super.onResume();
    }
}
