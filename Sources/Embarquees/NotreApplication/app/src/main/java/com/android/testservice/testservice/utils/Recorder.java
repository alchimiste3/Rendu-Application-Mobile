package com.android.testservice.testservice.utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by user on 29/11/16.
 */

public class Recorder {



    private String mFileName = null;
    private MediaRecorder mRecorder;
    private boolean EnEnregistrement = false;



    Context context = null;

    public Recorder(Context context, String mFileName){
        this.context = context;
        this.mFileName = mFileName;
    }


    public void recorder() {

        boolean perm = CheckPermissions.checkPermissionToRecord(context);
        if (perm) {

            Log.d("mRecorder", "mRecorder = " + mRecorder);

            if (!EnEnregistrement) {
                Toast.makeText(context, "Start Recorder!", Toast.LENGTH_SHORT).show();

                if (mRecorder == null) {
                    try {
                        mRecorder = new MediaRecorder();
                        mRecorder.reset();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setOutputFile(mFileName);

                        Log.d("mRecorder", "mRecorder = " + mRecorder);


                    } catch (Exception e) {
                        android.util.Log.e("[Monkey]", "Exception: " +
                                android.util.Log.getStackTraceString(e));
                    }


                    try {
                        mRecorder.prepare();
                    } catch (java.io.IOException ioe) {
                        android.util.Log.e("[Monkey]", "IOException: " +
                                android.util.Log.getStackTraceString(ioe));

                    } catch (java.lang.SecurityException e) {
                        android.util.Log.e("[Monkey]", "SecurityException: " +
                                android.util.Log.getStackTraceString(e));
                    }


                    try {
                        mRecorder.start();
                        EnEnregistrement = true;

                    } catch (java.lang.SecurityException e) {
                        android.util.Log.e("[Monkey]", "SecurityException: " +
                                android.util.Log.getStackTraceString(e));
                    }
                }

            } else {

                Toast.makeText(context, "Stop Recorder!", Toast.LENGTH_SHORT).show();


                try {
                    mRecorder.stop();


                } catch (java.lang.SecurityException e) {
                    android.util.Log.e("[Monkey]", "SecurityException: " +
                            android.util.Log.getStackTraceString(e));
                }


                EnEnregistrement = false;


                mRecorder.release();

                mRecorder = null;


            }
        }
        else{
            Log.d("PermService","Perm = "+perm);
        }
    }

    public boolean getEnEnregistrement() {
        return EnEnregistrement;
    }

    public void setEnEnregistrement(boolean enEnregistrement) {
        EnEnregistrement = enEnregistrement;
    }

    public MediaRecorder getmRecorder() {
        return mRecorder;
    }

    public void setmRecorder(MediaRecorder mRecorder) {
        this.mRecorder = mRecorder;
    }
}
