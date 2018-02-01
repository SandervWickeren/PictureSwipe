package com.example.sander.pictureswipe;

import android.net.Uri;

/**
 * Class that is used to upload the Uri in combination with the downloadUrl to Firebase.
 */
public class FirebaseImage {
    public String uri;
    public String downloadUrl;

    public FirebaseImage() {
        // Empty constructor necessary for Firebase.
    }

    public FirebaseImage(Uri uri, String downloadUrl) {
        this.uri = uri.toString();
        this.downloadUrl = downloadUrl;
    }
}