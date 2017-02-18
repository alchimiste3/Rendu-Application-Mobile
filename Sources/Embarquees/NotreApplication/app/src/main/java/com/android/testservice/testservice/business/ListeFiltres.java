package com.android.testservice.testservice.business;

/**
 * Created by user on 07/12/16.
 */
public class ListeFiltres{

    private boolean auRepos = true;
    private boolean interieur = true;
    private String jour = "lundi";


    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public boolean isAuRepos() {
        return auRepos;
    }

    public void setAuRepos(boolean auRepos) {
        this.auRepos = auRepos;
    }

    public boolean isInterieur() {
        return interieur;
    }

    public void setInterieur(boolean interieur) {
        this.interieur = interieur;
    }
}
