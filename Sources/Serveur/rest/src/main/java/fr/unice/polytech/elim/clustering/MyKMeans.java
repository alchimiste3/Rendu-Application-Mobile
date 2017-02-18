/* 
 * MyKMeans.java ;
 *
 * Pour creer cette classe, je suis partie de la solution de DataOnFocus :
 * http://www.dataonfocus.com/k-means-clustering-java-code/
 *
*/
package fr.unice.polytech.elim.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.unice.polytech.elim.clustering.AmplitudeWithLocation;
import fr.unice.polytech.elim.treatment.business.Amplitude;

/**
 * Cluster avec une liste d'amplitude, un centre et un id
 * @author Quentin Laborde
 *
 */
public class MyKMeans {

    private int NB_CLUSTERS = 3;    
    
    private int NB_POINTS = 15;
    
    //Min et Max de l'amplitude
    private static final int MIN_AMPL = 0;
    private static final int MAX_AMPL = 10;
    
    //Min et Max de lat et lon
    private static final int MIN_LAT_LON = 0;
    private static final int MAX_LAT_LON = 10;
    
    private List<AmplitudeWithLocation> listeAmplitudes;
    private List<MyCluster> listeClusters;
    
    public MyKMeans(int nbCluster) {
        this.listeAmplitudes = new ArrayList<AmplitudeWithLocation>();
        this.listeClusters = new ArrayList<MyCluster>();
        NB_CLUSTERS = nbCluster;
    }
    
    /**
     * Permet d'initialiser l'algo avant le calcule des itérations
     * @param liste
     */
    public void initKMeans(List<Amplitude> liste) {
        System.out.println("initKMeans avec mes amplitude");
        
        ///////////// Creation de la liste des amplitudes (points) /////////////
        
        listeAmplitudes = new ArrayList<AmplitudeWithLocation>();
        
        for(Amplitude amplitude : liste){
            AmplitudeWithLocation ampl = new AmplitudeWithLocation(amplitude.getAmplitude(), amplitude.getLat(), amplitude.getLon(), amplitude.getDate().getHours());
            listeAmplitudes.add(ampl);
        }
        
        
///////////// Creation des clusters avec leurs centres /////////////
        
        List<Integer> listeIndexCentre = new ArrayList<Integer>();
                
        // On creer les cluster et on leur assigne un centre au hasard
        for (int i = 0; i < NB_CLUSTERS; i++) {
            
            // On choisie un index pour le centre du cluster (qui n'est pas déja pris)
            int index = -1;
            while(index == -1 || listeIndexCentre.contains(index)){
                Random rand = new Random();
                index = rand.nextInt(listeAmplitudes.size());
                                
            }
            
            listeIndexCentre.add(index);
            
            // Creation du cluster et ajout du centre
            MyCluster cluster = new MyCluster(i);
            AmplitudeWithLocation centroid = listeAmplitudes.get(index);
            cluster.setCentre(centroid);
            
            listeClusters.add(cluster);
            
        }
        
        //displayClusters();
        
    }
    
    //Pour les tests du serveur
    public void initKMeans() {
        System.out.println("initKMeans avec des amplitude random");

        //Creation de listeAmplitudes
        listeAmplitudes = AmplitudeWithLocation.creationVecteurs(MIN_AMPL, MAX_AMPL, MIN_LAT_LON,MAX_LAT_LON,NB_POINTS);
        
        // On creer les cluster et on leur assigne un centre au hasard
        for (int i = 0; i < NB_CLUSTERS; i++) {
            
            MyCluster cluster = new MyCluster(i);
            
            AmplitudeWithLocation centroid = AmplitudeWithLocation.creationVecteur(MIN_AMPL, MAX_AMPL, MIN_LAT_LON,MAX_LAT_LON);
            
            cluster.setCentre(centroid);
            
            listeClusters.add(cluster);
            
        }
        
        //displayClusters();
    }
    

    private void displayClusters() {
        for (int i = 0; i < NB_CLUSTERS; i++) {
            MyCluster c = listeClusters.get(i);
            c.displayCluster();
        }
    }
    
    public String getClustersDisplay() {
        String res = "{\"data\" : [";
        for (int i = 0; i < NB_CLUSTERS - 1; i++) {
            MyCluster c = listeClusters.get(i);
            //c.displayCluster();
            res += c.toString() + ","; 
        }
        
        MyCluster c = listeClusters.get(NB_CLUSTERS - 1);
        //c.displayCluster();
        res += c.toString() + "]}"; 
        
        
        return res;
    }
    
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        
        JSONArray array = new JSONArray();
        for (int i = 0; i < NB_CLUSTERS; i++) {
            MyCluster c = listeClusters.get(i);
            //c.displayCluster();
            array.put(c.toJSON());
        }
        
        jsonObject.put("data", array);
        

        return jsonObject;
    }
    
    
    public void calculate() {
        
        System.out.println("calculate");

        boolean finish = false;
        int iteration = 0;
        
        while(!finish) {

            // On supprimer la liste des element du cluster (sauf le centre)
            clearClusters();
            
            // On get les centres des clusters
            List<AmplitudeWithLocation> centrePres = getCentres();

            assignationClusters();
            
            calculeCentre();
            
            iteration++;
            
            List<AmplitudeWithLocation> centreCourant = getCentres();
            
            //Calculates total distance between new and old Centroids
            double distance = 0;
            for(int i = 0; i < centrePres.size(); i++) {
                distance += AmplitudeWithLocation.distance((AmplitudeWithLocation)centrePres.get(i),(AmplitudeWithLocation)centreCourant.get(i));
            }
            
            
            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centre distances: " + distance);
            
            // TODO
            // displayClusters();
                        
            if(distance == 0) {
                finish = true;
            }
        }
    }
    
    private void clearClusters() {
        System.out.println("clearClusters");

        for(MyCluster cluster : listeClusters) {
            cluster.clear();
        }
    }
    
    private List<AmplitudeWithLocation> getCentres() {
        
        System.out.println("getCentres ");

        List<AmplitudeWithLocation> centres = new ArrayList<AmplitudeWithLocation>(NB_CLUSTERS);
        
        for(MyCluster cluster : listeClusters) {
            AmplitudeWithLocation aux = cluster.getCentre();
            AmplitudeWithLocation ampl = new AmplitudeWithLocation(aux.getAmpl(), aux.getLat(), aux.getLon(), aux.getHeur());
            centres.add(ampl);
        }
        
        return centres;
    }
    
    private void assignationClusters() {
        
        
        System.out.println("assignationClusters ");
        
        double max = Double.MAX_VALUE;
        double min = max; 
        int numCluster = 0;                 
        double distance = 0.0; 
        
        for(AmplitudeWithLocation ampl : listeAmplitudes) {
            min = max;
            for(int i = 0; i < NB_CLUSTERS; i++) {
                MyCluster c = listeClusters.get(i);
                
                distance = AmplitudeWithLocation.distance(ampl, c.getCentre());
                //System.out.println("distance : "+ distance);
                
                if(distance < min){
                    min = distance;
                    numCluster = i;
                }
            }
            ampl.setNumCluster(numCluster);
            listeClusters.get(numCluster).addAmplitude(ampl);
        }
    }
    
    private void calculeCentre() {
        
        System.out.println("calculeCentre ");

        for(MyCluster cluster : listeClusters) {
            
            double sumAmpl = 0;
            double sumLat = 0;
            double sumLon = 0;
            List<AmplitudeWithLocation> list = cluster.getListeAmplitudes();
            int nbAmplitude = list.size();
            
            for(AmplitudeWithLocation ampl : list) {
                sumAmpl += ampl.getAmpl();
                sumLat += ampl.getLat();
                sumLon += ampl.getLon();
            }
            
            AmplitudeWithLocation centre = cluster.getCentre();
            
            if(nbAmplitude > 0) {
                double newAmpl = sumAmpl / nbAmplitude;
                double newLat = sumLat / nbAmplitude;
                double newLon = sumLon / nbAmplitude;
                
                centre.setAmpl(newAmpl);
                centre.setLat(newLat);
                centre.setLon(newLon);

            }
        }
    }
}