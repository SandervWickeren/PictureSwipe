package com.example.sander.pictureswipe;

import android.content.Context;


/**
 * Class that contains helper functions that process the data before functions inside the
 * SqliteDatabase are being called.
 */
public class SqliteDatabaseHelper {

    private Context mContext;
    private SqliteDatabase db;


    public SqliteDatabaseHelper(Context context) {
        this.mContext = context;
        this.db = SqliteDatabaseSingleton.getInstance(mContext.getApplicationContext());
    }


    /**
     * Function that prepares and checks data before inserting it into the 'picture' table.
     * @param path of the image you want to add.
     */
    public void addToPictures(String path) {
        // Get name and album from path.
        String name = getSlice(path, 1);
        String album = getSlice(path, 2);

        // Add item if it's not already added.
        if (!(db.inPictures(name))) {
            db.insertPicture(name, album, path);
        }
    }


    /**
     * Function that handles when an item should be added to either bin / favorites. It first calls
     * addToPictures() to tell it has been reviewed. After that it calls that insertToList()
     * function to add it to the defined table.
     * @param path of the image you want to add.
     * @param table where you want to place the image (favorites / bin).
     */
    public void addToList(String path, String table) {
        // Add it to the pictures database.
        addToPictures(path);

        // Get name from path and insert to specified table.
        String name = getSlice(path, 1);
        db.insertToList(table, db.getIdFromName(name));
    }


    /**
     * Help function to get the correct strings.
     * @param path that has to be sliced.
     * @param slice the placed you want it to return, counted
     *              from the last place.
     * @return String containing the slice.
     */
    private String getSlice(String path, Integer slice) {
        String[] slicedPath = path.split("/");
        return slicedPath[slicedPath.length - slice];
    }
}