package com.example.sander.pictureswipe;

import android.content.Context;

/**
 * Created by sander on 30-1-2018.
 */

public class SqliteDatabaseHelper {

    private Context mContext;
    private SqliteDatabase db;


    public SqliteDatabaseHelper(Context context) {
        this.mContext = context;
        this.db = SqliteDatabaseSingleton.getInstance(mContext.getApplicationContext());
    }

    public void addToPictures(String path) {

        // Get name and album from path.
        String[] slicedPath = path.split("/");
        String name = getSlice(path, 1);
        String album = getSlice(path, 2);

        // Add item if it's not already added.
        if (!(db.inPictures(name))) {
            db.insertPicture(name, album, path);
        }
    }

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
