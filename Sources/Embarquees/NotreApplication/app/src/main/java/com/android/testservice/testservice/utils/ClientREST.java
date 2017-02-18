package com.android.testservice.testservice.utils;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientREST {



    private static String IP = "10.212.115.127";
    private static String port = "8181";

    private String URL = "http://"+IP+":"+port+"/cxf/demo/treatment";

    private JSONObject data = new JSONObject();

    public ClientREST(){

    }

    public ClientREST(JSONObject data){
        this.data = data;


    }

    public void sendAmplitudes() {
        Log.d("ClientREST", "sendAmplitudes URL = "+ "http://"+IP+":"+port+"/cxf/demo/treatment");

        URL = "http://"+IP+":"+port+"/cxf/demo/treatment";
        try {


            java.net.URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type","application/json");

                Log.d("connection", "data.toString().length() : "+ data.toString().length());



                connection.setFixedLengthStreamingMode(data.toString().length());
                DataOutputStream content = new DataOutputStream(connection.getOutputStream());
                content.writeBytes(data.toString());

                Log.d("connection", "connection : "+ connection.getResponseCode());

            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    public String getClusters(int nbClusters) {

        URL = "http://"+IP+":"+port+"/cxf/demo/treatment/clusters/"+nbClusters;
        return getClustersJSON();
    }



    public String getClusters(boolean auRepos, boolean interieur, int nbClusters) {
        URL = "http://"+IP+":"+port+"/cxf/demo/treatment/clusters/"+auRepos+"/"+ interieur +"/"+nbClusters;
        return getClustersJSON();

    }

    public String getClusters(boolean auRepos, boolean interieur, String jour, int nbClusters) {
        URL = "http://"+IP+":"+port+"/cxf/demo/treatment/clusters/"+jour+"/"+auRepos+"/"+interieur+"/"+nbClusters;
        return getClustersJSON();

    }


    public String getClustersJSON() {

        try {

            java.net.URL url = new URL(URL);

            Log.d("getClustersJSON", "URL : "+ URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-type","application/json");

                Log.d("getClusters", "connection : "+ connection.getResponseCode());

                InputStreamReader content = new InputStreamReader(connection.getInputStream());

                BufferedReader buffer = new BufferedReader(content);
                StringBuilder builder = new StringBuilder();
                String output;
                while ((output = buffer.readLine()) != null) {
                    builder.append(output);
                }

                Log.d("getClusters", "data : "+ builder.toString());
                return builder.toString();

            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;


    }




    public static String getIP() {
        return IP;
    }

    public static void setIP(String IP) {
        ClientREST.IP = IP;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        ClientREST.port = port;
    }


}
