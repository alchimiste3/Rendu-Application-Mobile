package fr.unice.polytech.elim.treatment.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.json.JSONObject;

import fr.unice.polytech.elim.treatment.utils.Day;


public class ListeAmplitudes {
    
    public Day jour;
    public List<Amplitude> listeAmplitudeJour = new ArrayList<Amplitude>();
    public List<Amplitude> listeAmplitudeNuit = new ArrayList<Amplitude>();


}
