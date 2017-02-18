package com.android.testservice.testservice.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 29/11/16.
 */

public class CheckPermissions {

    public static boolean checkPermissionInternet(final Context context) {

        boolean inter = ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;

        Log.d("checkPermissionService","\ninter = "+inter+"\n");

        return inter;

    }

    public static boolean checkPermissionLocation(final Context context) {

        boolean fineLoc = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        Log.d("checkPermissionService","\nfineLoc = "+fineLoc+"\n");

        return fineLoc;

    }


    public static boolean checkPermissionToRecord(final Context context) {

        boolean write = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean record = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        Log.d("checkPermissionToRecord","\nwrite = "+write+"\nrecord = "+record+"\n");

        return write && record;

    }
}
