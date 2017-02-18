package com.android.testservice.testservice.business;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by user on 07/12/16.
 */
public class Amplitude implements Serializable{

    private double ampl;
    private double lat;
    private double lon;
    private Date date;


    public Amplitude(double ampl, double lat, double lon) {
        this.ampl = ampl;
        this.lat = lat;
        this.lon = lon;
        this.date = null;
    }

    public Amplitude(double ampl, double lat, double lon, Date date) {
        this.ampl = ampl;
        this.lat = lat;
        this.lon = lon;
        this.date = date;
    }

    public double getAmpl() {
        return ampl;
    }

    public void setAmpl(double ampl) {
        this.ampl = ampl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Amplitude{" +
                "ampl=" + ampl +
                ", lat=" + lat +
                ", lon=" + lon +
                ", date=" + date +
                '}';
    }
}
