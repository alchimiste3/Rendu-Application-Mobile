/* 
 * AmplitudeWithLocation.java ;
 *
 * Pour creer cette classe, je suis partie de la solution de DataOnFocus :
 * http://www.dataonfocus.com/k-means-clustering-java-code/
 *
*/

package fr.unice.polytech.elim.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

/**
 * Vecteur de 3 dimentions 
 * @author Quentin Laborde
 *
 */
public class AmplitudeWithLocation {

    private double ampl = 0;
    private double lat = 0;
    private double lon = 0;
    private int heur = 11;

    private int numClusters = 0;
    
    
    private static double coefAmpl = 1000;
    private static double coefLat = 10000000;
    private static double coefLon = 10000000;
    private static double coefHeur = 0.5;
    

    public AmplitudeWithLocation(double ampl, double lat, double lon, int heur)
    {
        this.setAmpl(ampl);
        this.setLat(lat);
        this.setLon(lon);
        this.setHeur(heur);
    }
    
    
    //On calcule la distance entre deux vecteurs (ampl,lat,lon)
    protected static double distance(AmplitudeWithLocation vecteur, AmplitudeWithLocation centre) {


        
        return Math.sqrt(Math.pow(coefAmpl*(centre.getAmpl() - vecteur.getAmpl()), 2) + Math.pow(coefLon*(centre.getLon() - vecteur.getLon()), 2) + Math.pow(coefLat*(centre.getLat() - vecteur.getLat()), 2) + Math.pow(coefHeur * (vecteur.getHeur() - centre.getHeur()), 2));
    }
    
    
    
    public static AmplitudeWithLocation creationVecteur(int minAmpl, int maxAmpl, int minLatLon, int maxLatLon) {
        Random r = new Random();
        
        double ampl = minAmpl + (maxAmpl - minAmpl) * r.nextDouble();
        double lat = minLatLon + (maxLatLon - minLatLon) * r.nextDouble();
        double lon = minLatLon + (maxLatLon - minLatLon) * r.nextDouble();
        
        return new AmplitudeWithLocation(ampl, lat, lon, 11);
    }
    
    public static List<AmplitudeWithLocation> creationVecteurs(int minAmpl, int maxAmpl, int minLatLon, int maxLatLon, int nombre) {
        List<AmplitudeWithLocation> listVecteurs = new ArrayList<AmplitudeWithLocation>(nombre);
        
        for(int i = 0; i < nombre; i++) {
            listVecteurs.add(creationVecteur(minAmpl, maxAmpl, minLatLon, maxLatLon));
        }
        
        return listVecteurs;
    }
    
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amplitude", ampl);
        jsonObject.put("lat", lat);
        jsonObject.put("lon", lon);
        jsonObject.put("heur", heur);
        
        return jsonObject;
    }
    
    public String toString() {
        return "{\"amplitude\":"+ampl+",\"lat\":"+lat+",\"lon\":"+lon+",\"heur\":"+heur+"}";
    }
    
    
    
    
    /////////////////////////// Getters et Setters ///////////////////////////

    
    public double getAmpl() {
        return ampl;
    }

    public void setAmpl(double ampl) {
        this.ampl = ampl;
    }

    public double getLat()  {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
    
    public double getLon() {
        return this.lon;
    }
    
    public void setLon(double lon) {
        this.lon = lon;
    }
   
    public int getNumCluster() {
        return this.numClusters;
    }
    
    public void setNumCluster(int n) {
        this.numClusters = n;
    }


    public int getHeur() {
        return heur;
    }


    public void setHeur(int heur) {
        this.heur = heur;
    }
    
    
    

    
}