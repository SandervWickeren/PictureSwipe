package com.example.sander.pictureswipe;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the loading and processing of images from the local storage.
 */

public class LoadImages {

    private Context mContext;

    public LoadImages(Context context) {
        this.mContext = context;
    }

    public List<String> getList(Context context, Uri uri) {
        String initialPath = getRealPathFromUri(context, uri);
        return genList(initialPath);
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

    public Boolean checkDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()){
            System.out.println("No such directory");
            return Boolean.FALSE;
        } else if (!dir.canRead()) {
            System.out.println("Can't read");
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public void removeFromPictures(String name) {

        // Get database instance and remove item by name.
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(mContext.getApplicationContext());
        db.deleteAlbumFromPictures(name);
    }

    /**
     * Uses a path from an images to index all files
     * from its parent folder. It restricts to only
     * the images that aren't reviewed before.
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

        // Empty list to save images
        List<String> imagePaths = new ArrayList<>();

        if (checkDir(dir)) {

            // Supported file extensions.
            String[] allowedExtensions = new String[] {"jpg", "jpeg", "png"};
            File[] files = dir.listFiles();

            // Get database instance
            SqliteDatabase db = SqliteDatabaseSingleton.getInstance(mContext.getApplicationContext());

            // Convert to list of string containing only allowed file extensions and
            // check if not already reviewed.
            for (File img:files){
                for (String extension : allowedExtensions) {

                    // Check if valid extension
                    Boolean allowed = img.getName().toLowerCase().endsWith(extension);

                    // Add image to list if picture is valid and not in pictures table.
                    if (allowed && !(db.inPictures(img.getName()))) {
                        imagePaths.add(img.toString());
                    }
                }
            }
        }

        return imagePaths;
    }
}
