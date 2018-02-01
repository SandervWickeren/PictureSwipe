package com.example.sander.pictureswipe;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Class that handles all permission requests, and the
 * corresponding response.
 */
public class GrandPermissions {

    public void checkReadPermission(Activity activity, int request) {
        // Get the read permission status.
        int readPermission = ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        // If no permission, launch request.
        if (readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, request);
        }
    }

    public void checkWritePermission(Activity activity, int request) {
        // Get write permission status.
        int writePermission = ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If no permission launch request.
        if (writePermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request);
        }
    }
}