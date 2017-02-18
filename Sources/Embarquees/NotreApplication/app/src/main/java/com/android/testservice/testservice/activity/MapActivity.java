package com.android.testservice.testservice.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.testservice.testservice.R;
import com.android.testservice.testservice.business.WindowAdapter;
import com.android.testservice.testservice.utils.Position;
import com.android.testservice.testservice.business.Amplitude;
import com.android.testservice.testservice.business.Cluster;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity   implements OnMapReadyCallback {
    ArrayList<Cluster> listeClusters;
    HashMap<Position, String> hashPositions;
    GoogleMap map;
    boolean MODE_DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        hashPositions = new HashMap<>();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        listeClusters = (ArrayList<Cluster>)bundle.getSerializable("clusters");


        // Fusion des clusters inclus
        /*
       ArrayList<Cluster>listeClustersFusion = new ArrayList<>();
      //  listeClustersFusion.addAll(listeClusters);

       for (Cluster c1 : listeClusters) {
            for (Cluster c2: listeClusters) {
                Location locC1 = new Location("");
                locC1.setLongitude(c1.getCentre().getLon());
                locC1.setLatitude(c1.getCentre().getLat());

                Location locC2 = new Location("");
                locC2.setLongitude(c2.getCentre().getLon());
                locC2.setLatitude(c2.getCentre().getLat());

                final float distanceC1C2 = locC1.distanceTo(locC2);
                if ((distanceC1C2 + getHighestDistance(c2.getCentre(), (ArrayList<Amplitude>) c2.getListeAmplitude())) <
                        getHighestDistance(c1.getCentre(), (ArrayList<Amplitude>) c1.getListeAmplitude())) {
                    // Fusion - c2 inclus dans c1
                    ArrayList<Amplitude> total = (ArrayList<Amplitude>) c1.getListeAmplitude();
                    total.addAll(c2.getListeAmplitude());
                    c1.setListeAmplitude(total);
                }
                else {
                }
            }
        }
        listeClusters = listeClustersFusion;

        */
        // Pre traitement pour les info bulles
        for (Cluster c : listeClusters) {
            Amplitude centre = c.getCentre();

            String centreTexte;
            Position p = new Position(centre.getLon(), centre.getLat());
            if (hashPositions.containsKey(p))
                centreTexte = hashPositions.get(p) + "\n Centre = " + centre.getAmpl();
            else centreTexte = " Centre = " + centre.getAmpl();
            hashPositions.put(p, centreTexte);

            ArrayList<Amplitude> listeAmplitudes = (ArrayList<Amplitude>) c.getListeAmplitude();
            for (Amplitude a : listeAmplitudes) {
                String res;
                Position pA = new Position(a.getLon(), a.getLat());
                if (hashPositions.containsKey(pA))
                    res = hashPositions.get(pA) + "\n Amplitude = " + a.getAmpl();
                else res = " Amplitude = " + a.getAmpl();
                hashPositions.put(pA, res);
            }

        }
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        map.setInfoWindowAdapter(new WindowAdapter(getApplicationContext()));
        //On centre la camera sur la première amplitude
        if (listeClusters.size() > 0) {
            Cluster cluster = listeClusters.get(0);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cluster.getCentre().getLat(), cluster.getCentre().getLon()), 11));

        }
        for (Cluster c : listeClusters) {
            Amplitude centre = c.getCentre();
            ArrayList<Amplitude> listeAmplitudes = (ArrayList<Amplitude>) c.getListeAmplitude();
            //On ajoute le centre du cluster
            if (MODE_DEBUG)
                addCentre(centre);
            //On trace le cercle
            float[] hsvColor = new float[3];
            hsvColor[0] = getAverageColor(listeAmplitudes);
            hsvColor[1] = 50;
            hsvColor[2] = 50;
            map.addCircle(new CircleOptions()
                    .center(new LatLng(centre.getLat(), centre.getLon()))
                    .radius(getHighestDistance(centre, listeAmplitudes) + 1)
                    .strokeColor(Color.HSVToColor(50, hsvColor))
                    .zIndex((float)centre.getAmpl())
                    .fillColor(Color.HSVToColor(50, hsvColor)));
            for (Amplitude a : listeAmplitudes) {
            //    addAmplitude(a);
            }
        }
    }

    void addAmplitude(Amplitude a ) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(a.getLat(), a.getLon()))
                    .title(hashPositions.get(new Position(a.getLon(), a.getLat())))
                    .icon(BitmapDescriptorFactory .defaultMarker(getColor(a.getAmpl()))));
                  //  .icon(getMarkerIcon("##ff0000")));
    }

    void addCentre(Amplitude centre) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(centre.getLat(), centre.getLon()))
                .title(hashPositions.get(new Position(centre.getLon(), centre.getLat())))
                .icon(BitmapDescriptorFactory .defaultMarker(getColor(centre.getAmpl()))));
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public int getColor(double amplitude) {
        double a = (90 - amplitude)*2;
        if (a < 0)
            a = 300  + a;
        return (int) a;
    }

    public int getAverageColor(ArrayList<Amplitude> listeAmplitudes) {
        double sum = 0;
        for (Amplitude a : listeAmplitudes) {
            sum += a.getAmpl();
        }
        sum /= listeAmplitudes.size();
        return getColor(sum);
    }

    public double getHighestDistance(Amplitude centre, ArrayList<Amplitude> listeAmplitudes) {
        Location locationCentre = new Location("");
        locationCentre.setLongitude(centre.getLon());
        locationCentre.setLatitude(centre.getLat());
    //    Amplitude amplitudeMax;
        double distanceMax = 0;
        for (Amplitude a : listeAmplitudes) {
            Location locationAmplitude = new Location("");
            locationAmplitude.setLongitude(a.getLon());
            locationAmplitude.setLatitude(a.getLat());
            if (locationCentre.distanceTo(locationAmplitude) > distanceMax) {
            //    amplitudeMax = a;
                distanceMax = locationCentre.distanceTo(locationAmplitude);
            }
        }
        return distanceMax;
    }
}