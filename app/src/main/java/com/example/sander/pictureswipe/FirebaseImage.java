package com.example.sander.pictureswipe;

import android.net.Uri;

/**
 * Class that defines
 */

public class FirebaseImage {
    public String uri;
    public String downloadUrl;

    public FirebaseImage() {}

    public FirebaseImage(Uri uri, String downloadUrl) {
        this.uri = uri.toString();
        this.downloadUrl = downloadUrl;
    }
}
