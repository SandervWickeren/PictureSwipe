package com.example.sander.pictureswipe;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the loading and processing of images from the local storage.
 */

public class LoadImages {

    public List<String> getList(Context context, Uri uri) {
        String initial_path = getRealPathFromUri(context, uri);
        return genList(initial_path);
    }

    /**
     * Used to convert URI to a real path.
     * Source: https://stackoverflow.com/a/20059657
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Uses a path from an images to index all files
     * from its parent folder.
     * @param path Path from the selected images.
     * @return List of files from the parent folder.
     */
    public List<String> genList(String path) {

        // Get base folder from selected image.
        String[] splitPath = path.split("/");
        String dirPath = "";
        for (int i = 0; i < splitPath.length - 1; i++) {
            dirPath += splitPath[i] + "/";
        }
        System.out.println(dirPath);

        // Use base folder to list all files from directory.
        File dir = new File(dirPath);

        // Check for possible failures.
        if (!dir.exists() || !dir.isDirectory()){
            System.out.println("No such directory");
        } else if (!dir.canRead()) {
            System.out.println("Can't read");
        }

        // Supported file extensions.
        String[] allowedExtensions = new String[] {"jpg", "jpeg", "png"};
        File[] files = dir.listFiles();

        // Convert to list of string containing only allowed files.
        List<String> imagePaths = new ArrayList<>();
        for (File img:files){
            for (String extension : allowedExtensions) {
                if (img.getName().toLowerCase().endsWith(extension)) {
                    imagePaths.add(img.toString());
                }
            }
        }

        return imagePaths;
    }
}
