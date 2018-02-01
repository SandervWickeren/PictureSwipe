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
     * @param context application context.
     * @param contentUri of the image.
     * @return String containing the path of the given image.
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            // Get all image data.
            String[] proj = { MediaStore.Images.Media.DATA };

            // Query image data to find the path using the uri.
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

            // Get value from query.
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
     * Function that check if a certain file / directory exists.
     * @param dir file you want to check.
     * @return True if exists, false if it doesn't exists or isn't readable.
     */
    public Boolean checkDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()){
            return Boolean.FALSE;
        } else if (!dir.canRead()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }


    /**
     * Uses a path from an image to index all files
     * from its parent folder. It restricts to only
     * the images that aren't reviewed before.
     * @param path Path from the selected images.
     * @return List of files from the parent folder.
     */
    public List<String> genList(String path) {

        // Get base folder from selected image by stripping the name from the path.
        String[] splitPath = path.split("/");
        String dirPath = "";
        for (int i = 0; i < splitPath.length - 1; i++) {
            dirPath += splitPath[i] + "/";
        }

        // Use base folder to list all files from directory.
        File dir = new File(dirPath);

        // Empty list to save images
        List<String> imagePaths = new ArrayList<>();

        // Check if directory exists.
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