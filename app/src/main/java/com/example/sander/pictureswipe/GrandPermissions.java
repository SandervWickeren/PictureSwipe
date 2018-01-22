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

        int readPermission = ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, request);
        }
    }

    // Response. https://developer.android.com/training/permissions/requesting.html#java
}
