package com.android.testservice.testservice.business;

/**
 * Created by user on 03/12/16.
 */

public class Day {

//    lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche;

    public static String getDay(int position){
        switch (position){
            case 1:
                return "lundi";
            case 2:
                return "mardi";
            case 3:
                return "mercredi";
            case 4:
                return "jeudi";
            case 5:
                return "vendredi";
            case 6:
                return "samedi";
            case 7:
                return "dimanche";
            default:
                return "null";
        }

    }


}
