package com.android.testservice.testservice.write_read;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by user on 29/11/16.
 */

public class WriteReadAmplitudes extends WriteReadFile{


    @Override
    public void cleanFile() {
        super.cleanFile();

    }

    public void addAmplitude(double amplitude, double lat, double lon, boolean auRepos, boolean interieur, Date date){
        writeData("\naddAmplitude", true);
        try {
            /*
            String data = readData(NameFiles.AMPLITUDE_FILE);
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
*/
            JSONObject newAmpl = new JSONObject();
            newAmpl.put("amplitude",amplitude);
            newAmpl.put("lat",lat);
            newAmpl.put("lon",lon);
            newAmpl.put("date",date.getTime());
            newAmpl.put("auRepos",auRepos);
            newAmpl.put("interieur",interieur);

            if(isEmpty(NameFiles.AMPLITUDE_FILE)){
                writeData(newAmpl.toString(), true, NameFiles.AMPLITUDE_FILE.toString());
            }
            else {
                writeData(","+newAmpl.toString(), true, NameFiles.AMPLITUDE_FILE.toString());
            }

            //writeData("\n", true);


        } catch (JSONException e) {
            writeData("JSONException → "+e, true);
            e.printStackTrace();
        }

    }

    public JSONObject getAmplitudesJSON(){

        try {
            String res = readData(NameFiles.AMPLITUDE_FILE);
            res = "[" + res + "]";
            JSONArray jsonArray = new JSONArray(res);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", jsonArray);

            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }


}
