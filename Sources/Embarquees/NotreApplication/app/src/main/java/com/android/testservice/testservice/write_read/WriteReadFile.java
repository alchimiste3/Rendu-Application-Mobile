package com.android.testservice.testservice.write_read;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 29/11/16.
 */

public class WriteReadFile {


    private String nameFile = NameFiles.DEBUG_FILE.toString();


    private File openFile(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), nameFile);
        return file;
    }


    public void writeData(String data, boolean aLaSuite){
        File file = openFile();

        try {
            FileOutputStream stream = new FileOutputStream(file, aLaSuite);
            stream.write(data.getBytes());
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String readData(){
        Log.d("readData", "readData");
        File file = openFile();
        int length = (int) file.length();
        byte[] bytes = new byte[length];

        Log.d("readData", "length = " + length);

        try {
            FileInputStream stream = new FileInputStream(file);
            stream.read(bytes);
            stream.close();

            Log.d("readData", "new String(bytes)  = " + new String(bytes));
            Log.d("readData", "new String(bytes)  = " + new String(bytes).length());

            return new String(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Erreur";
    }

    public void writeData(String data, boolean aLaSuite, String nameFile){
        this.nameFile = nameFile;
        writeData(data,aLaSuite);
        this.nameFile = NameFiles.DEBUG_FILE.toString();
    }

    public String readData(NameFiles nameFile){
        this.nameFile = nameFile.toString();
        String rep = readData();
        this.nameFile = NameFiles.DEBUG_FILE.toString();
        return rep;
    }


    public void cleanFile(){

        for(NameFiles n : NameFiles.values()){
            //Log.d("cleanFile", "n = " + n.toString());
            writeData("",false,n.toString());
        }
    }

    public void cleanFile(String name){

        writeData("",false,name);

    }

    public boolean isEmpty(NameFiles nameFiles){
        writeData("\nisEmpty",true);

        String res = readData(nameFiles);

        return (res.equals(""));
    }
}
