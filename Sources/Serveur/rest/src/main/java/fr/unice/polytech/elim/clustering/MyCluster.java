/* 
 * MyCluster.java ;
 *
 * Pour creer cette classe, je suis partie de la solution de DataOnFocus :
 * http://www.dataonfocus.com/k-means-clustering-java-code/
 *
*/

package fr.unice.polytech.elim.clustering;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Cluster avec une liste d'amplitude, un centre et un id
 * @author Quentin Laborde
 *
 */
public class MyCluster {
    
    public List<AmplitudeWithLocation> listeAmplitudes;
    public AmplitudeWithLocation centre;
    public int id;
    
    //Creates a new Cluster
    public MyCluster(int id) {
        this.id = id;
        this.listeAmplitudes = new ArrayList<AmplitudeWithLocation>();
        this.centre = null;
    }

    
    public void addAmplitude(AmplitudeWithLocation ampl) {
        listeAmplitudes.add(ampl);
    }
    
    
    public void clear() {
        listeAmplitudes.clear();
    }



    public void displayCluster() {
        System.out.println("[Cluster: " + id+"]");
        System.out.println("[Centroid: " + centre + "]");
        System.out.println("[Points: \n");
        for(AmplitudeWithLocation p : listeAmplitudes) {
            System.out.println(p);
        }
        System.out.println("]");
    }
    
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("idCluster", id);
        jsonObject.put("centre", centre.toJSON());
        
        JSONArray array = new JSONArray();
        for(AmplitudeWithLocation p : listeAmplitudes) {
            array.put(p.toJSON());
        }
        
        jsonObject.put("points", array);

        return jsonObject;
    }

    @Override
    public String toString() {
        
        String res = "";
        
        res += "{\"idCluster\" : "+id+",";
        res += "\"Centre\" : "+centre+",";
        res += "\"Points\": [";

        for(AmplitudeWithLocation p : listeAmplitudes) {
            res += p;
        }
        res += "]}";
        
       
        return res;
        
    }
    
    /////////////////////////// Getters et Setters ///////////////////////////
    
    public List<AmplitudeWithLocation> getListeAmplitudes() {
        return listeAmplitudes;
    }



    public void setListeAmplitudes(List<AmplitudeWithLocation> listeAmplitudes) {
        this.listeAmplitudes = listeAmplitudes;
    }



    public AmplitudeWithLocation getCentre() {
        return centre;
    }



    public void setCentre(AmplitudeWithLocation centre) {
        this.centre = centre;
    }



    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }
    
    

}