package com.android.testservice.testservice.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 07/12/16.
 */
public class Cluster implements Serializable{

    private Amplitude centre =null;
    private List<Amplitude> listeAmplitude= new ArrayList<Amplitude>();

    public List<Amplitude> getListeAmplitude() {
        return listeAmplitude;
    }

    public void setListeAmplitude(List<Amplitude> listeAmplitude) {
        this.listeAmplitude = listeAmplitude;
    }

    public Amplitude getCentre() {
        return centre;
    }

    public void setCentre(Amplitude centre) {
        this.centre = centre;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "centre=" + centre +
                ", listeAmplitude=" + listeAmplitude +
                '}';
    }
}
