package fr.unice.polytech.elim.treatment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.unice.polytech.elim.clustering.MyCluster;
import fr.unice.polytech.elim.clustering.MyKMeans;
import fr.unice.polytech.elim.treatment.business.Amplitude;
import fr.unice.polytech.elim.treatment.business.ListeAmplitudes;
import fr.unice.polytech.elim.treatment.utils.Day;
import fr.unice.polytech.elim.treatment.utils.Time;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


@Path("/treatment")
@Produces(MediaType.APPLICATION_JSON)
public class Treatment {
    
    public static List<Amplitude> listeAmplitude = new ArrayList<Amplitude>();

    public static HashMap<Day, ArrayList<Amplitude>> map = new HashMap<Day, ArrayList<Amplitude>>();

    public Treatment() {
       System.out.println("Constructeur : chargement des données !!!");
       

       System.out.println("Current relative path is: " + Paths.get(".").toAbsolutePath().normalize().toString());
       
       File file = new File("dataAmplitudes/data");
       
       try {

            FileInputStream fileInputStream = new FileInputStream(file);
            
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            
            fileInputStream.read(bytes);
            
            fileInputStream.close();
                        
            addAmplitudes(new String(bytes));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       
    }
    
    
    @POST
    public void addAmplitudes(String body) {
        System.out.println("addAmplitudes");

        JSONObject obj = new JSONObject(body);
        
        
        // System.out.println("Json amplitudes String = "+ body);
        
        JSONArray array = obj.getJSONArray("data");
        
        for(int i = 0; i < array.length(); i++){
            
            JSONObject ampl = array.getJSONObject(i);
            Amplitude amplitude = new Amplitude();
            amplitude.setAmplitude(ampl.getDouble("amplitude"));
            amplitude.setLat(ampl.getDouble("lat"));
            amplitude.setLon(ampl.getDouble("lon"));
            amplitude.setAuRepos(ampl.getBoolean("auRepos"));
            amplitude.setInterieur(ampl.getBoolean("interieur"));

            Date date = new Date(ampl.getLong("date"));
            amplitude.setDate(date);
            
            Day jour = getDay(date);
            

            if(!map.containsKey(jour)){
               map.put(jour, new ArrayList<Amplitude>()); 
            }
            
            map.get(jour).add(amplitude);            
          
            listeAmplitude.add(amplitude);
        }
        
        
        ///////////////////// Mise a jour des donner dans le fichier /////////////////////
        // La permiere est mise a jour ne saire a rien
        
        
        try {
            File file = new File("dataAmplitudes/data");
            
            JSONObject jsonObject = new JSONObject();
            JSONArray a = new JSONArray();
                    
            for(Amplitude ampl : listeAmplitude){
                a.put(ampl.toJSON());
            }
            
            jsonObject.put("data", a);
            
            FileOutputStream fileOutputStream;
            
            fileOutputStream = new FileOutputStream(file, false);
            
            fileOutputStream.write(jsonObject.toString().getBytes());
            fileOutputStream.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

                
        //displayMap();
    }
    

    
    
    @Path("/clusters/{nbClusters}")
    @GET
    public Response getClusters(@PathParam("nbClusters") int nbClusters) {

        
        
        System.out.println("nbClusters = "+ nbClusters);

        String rep = doKMeans(listeAmplitude,nbClusters);
        
        if(rep.equals("{}")){
            doKMeansRandom(nbClusters);
        }
        
        return Response.ok(rep).build();
    }
    
    @Path("/clusters/{day}/{auRepos}/{interieur}/{nbClusters}")
    @GET
    public Response getClusters(@PathParam("day") String day, @PathParam("auRepos") boolean auRepos, @PathParam("interieur") boolean interieur,@PathParam("nbClusters") int nbClusters) {
        
        System.out.println("day = "+ day);
        System.out.println("auRepos = "+ auRepos);
        System.out.println("interieur = "+ interieur);
        System.out.println("nbClusters = "+ nbClusters);

        try{
            Day jour = Day.valueOf(day);
            System.out.println("jour = "+jour);
            ArrayList<Amplitude> liste = new ArrayList<Amplitude>();
            
            System.out.println("map.containsKey(jour) = "+map.containsKey(jour));
            if(map.containsKey(jour)){
                liste = map.get(jour);

            }
            
            System.out.println("liste.size() = "+ liste.size() );

            liste = filterAuRepos(liste, auRepos);
            
            
            System.out.println("liste.size() apres auRepos = "+ liste.size() );

            liste = filterInterieur(liste, interieur);
            
            System.out.println("liste.size() apres interieur = "+ liste.size() );


            String rep = doKMeans(liste,nbClusters);
            

            return Response.ok(rep).build();
        }
        catch (IllegalArgumentException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("erreur", "Premier argument invalide (IllegalArgumentException");
            return Response.ok(jsonObject.toString()).build();
        }
        catch (NullPointerException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("erreur", "Premier argument invalide (NullPointerException");
            return Response.ok(jsonObject.toString()).build();
        }

    }
    
    @Path("/clusters/{auRepos}/{interieur}/{nbClusters}")
    @GET
    public Response getClusters(@PathParam("auRepos") boolean auRepos,@PathParam("interieur") boolean interieur,@PathParam("nbClusters") int nbClusters) {
        
        System.out.println("auRepos = "+ auRepos);
        System.out.println("interieur = "+ interieur);
        System.out.println("nbClusters = "+ nbClusters);

        ArrayList<Amplitude> liste = new ArrayList<Amplitude>();

        for(ArrayList<Amplitude> l : map.values()){
            liste.addAll(l);
        }
        
        System.out.println("liste.size() = "+ liste.size() );

 
        liste = filterAuRepos(liste, auRepos);

        System.out.println("liste.size() apres repos = "+ liste.size() );

        liste = filterInterieur(liste, interieur);
        
        System.out.println("liste.size() apres interieur = "+ liste.size() );

        
        String rep = doKMeans(liste,nbClusters);

        return Response.ok(rep).build();

        
    }
    

    
    @Path("/amplitudes")
    @GET
    public Response getAmplitude() {
        
        System.out.println("getAmplitude");
        
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
                
        for(Amplitude ampl : listeAmplitude){
            array.put(ampl.toJSON());
        }
        
        jsonObject.put("data", array);
        
        return Response.ok(jsonObject.toString()).build();
    }
    
    
    ///////////////////////////// Methodes private /////////////////////////////
    
    private String doKMeans(List<Amplitude> liste, int nbClusters){
        MyKMeans kmeans = new MyKMeans(nbClusters);
        
        System.out.println("!liste.isEmpty() && (liste.size() >= nbClusters) = "+ (!liste.isEmpty() && (liste.size() >= nbClusters)));
        
        System.out.println("nbClusters = "+ nbClusters);
        System.out.println("liste.size() = "+ liste.size() );

        System.out.println("(liste.size() >= nbClusters) = "+ (liste.size() >= nbClusters));

        if(!liste.isEmpty() && (liste.size() >= nbClusters)){
            kmeans.initKMeans(liste);
            kmeans.calculate();
            return kmeans.toJSON().toString();
        }
        else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", "Pas assez d'amplitudes pour calculer "+ nbClusters +" cluster(s)");
            return jsonObject.toString();
        }
    }
    
    private String doKMeansRandom(int nbClusters){
        MyKMeans kmeans = new MyKMeans(nbClusters);

        kmeans.initKMeans();
        kmeans.calculate();
        return kmeans.toJSON().toString();

    }
    
    private Day getDay(Date date){
        Day jour = Day.lundi;
        switch (date.getDay()) {
        case 1:
            jour = Day.lundi;
            break;
        case 2:
            jour = Day.mardi;
            break;
        case 3:
            jour = Day.mercredi;
            break;
        case 4:
            jour = Day.jeudi;
            break;
        case 5:
            jour = Day.vendredi;
            break;
        case 6:
            jour = Day.samedi;
            break;
        case 7:
            jour = Day.dimanche;
            break;
        default:
            break;
        }
        
        return jour;
    }
    

    private ArrayList<Amplitude> filterAuRepos(ArrayList<Amplitude> liste, boolean auRepos){
        
        ArrayList<Amplitude> newList = new ArrayList<Amplitude>();
        
        for(Amplitude ampl : liste){
            
            boolean auRep = ampl.isAuRepos();
            //System.out.println("auRep = "+ auRep);
            
            if(auRepos == auRep){
                newList.add(ampl);         
            }
        }
        
        return newList;
        
    }
    
    private ArrayList<Amplitude> filterInterieur(ArrayList<Amplitude> liste, boolean interieur){
        
        ArrayList<Amplitude> newList = new ArrayList<Amplitude>();
        
        for(Amplitude ampl : liste){
            
            
            boolean inter = ampl.isInterieur();
            
            
            if(interieur == inter){
                newList.add(ampl);         
            }
        }
        
        return newList;
        
    }
    
    private void displayMap(){
        
        Iterator<Day> it = map.keySet().iterator();
        for(;it.hasNext();){
            Day jour = it.next();
            System.out.println("Pour "+ jour.toString() + " : \n");
            System.out.println(map.get(jour));


        }
    }
    

}


