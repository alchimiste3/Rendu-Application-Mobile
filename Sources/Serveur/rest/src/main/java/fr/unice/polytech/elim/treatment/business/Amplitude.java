package fr.unice.polytech.elim.treatment.business;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.json.JSONObject;

@XmlType
public class Amplitude {
    
    private Date date;
    private double lon;
    private double lat;
    private double amplitude;
    
    private boolean auRepos;
    private boolean interieur;


    @XmlElement
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement
    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @XmlElement
    public double getLat() {
        return lat;
    }

    
    public void setLat(double lat) {
        this.lat = lat;
    }
    
    @XmlElement
    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }
    
    
    @XmlElement
    public boolean isAuRepos() {
        return auRepos;
    }

    public void setAuRepos(boolean auRepos) {
        this.auRepos = auRepos;
    }
    
    
    @XmlElement
    public boolean isInterieur() {
        return interieur;
    }

    public void setInterieur(boolean interieur) {
        this.interieur = interieur;
    }

    @Override
    public String toString() {
        return "Amplitude [date=" + date + ", lon=" + lon + ", lat=" + lat
                + ", amplitude=" + amplitude + ", heur=" + date.getHours() + ", auRepos=" + auRepos + ", interieur=" + interieur + "]";
    }
    
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("date", date.getTime());
        jsonObject.put("amplitude", amplitude);
        jsonObject.put("lat", lat);
        jsonObject.put("lon", lon);
        jsonObject.put("heur", date.getHours());
        jsonObject.put("auRepos", auRepos);
        jsonObject.put("interieur", interieur);

        return jsonObject;
    }

}
