package com.android.testservice.testservice.utils;

import android.util.Log;

import com.android.testservice.testservice.business.Amplitude;
import com.android.testservice.testservice.business.Cluster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 29/11/16.
 */

public class JSONTrans {

    public static List<Cluster> getListCluster(String jsonString) {

        ArrayList<Cluster> listeCluster = new ArrayList<Cluster>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray array = (JSONArray) jsonObject.get("data");

            for(int i = 0 ; i < array.length(); i++){
                JSONObject clusterJson = (JSONObject) array.get(i);
                Log.d("listeCluster", "clusterJson = " + clusterJson.toString());
                Cluster cluster = new Cluster();
                JSONObject centreJson = (JSONObject) clusterJson.get("centre");
                JSONArray listeAmplitudeJson = (JSONArray) clusterJson.get("points");

                cluster.setCentre(new Amplitude(Double.valueOf(centreJson.get("amplitude").toString()),Double.valueOf(centreJson.get("lat").toString()),Double.valueOf(centreJson.get("lon").toString())));

                for(int y = 0 ; y < listeAmplitudeJson.length(); y++){
                    JSONObject amplitudeJson = (JSONObject) listeAmplitudeJson.get(y);
                    Amplitude amplitude = new Amplitude(Double.valueOf(amplitudeJson.get("amplitude").toString()),Double.valueOf(amplitudeJson.get("lat").toString()),Double.valueOf(amplitudeJson.get("lon").toString()));
                    cluster.getListeAmplitude().add(amplitude);
                }

                listeCluster.add(cluster);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listeCluster;

    }

}
