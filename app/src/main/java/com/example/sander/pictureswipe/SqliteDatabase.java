package com.example.sander.pictureswipe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that is used to manage the local databases.
 */

public class SqliteDatabase extends SQLiteOpenHelper {

    public SqliteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Pictures table
        sqLiteDatabase.execSQL("CREATE TABLE pictures (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT, album TEXT);");

        // Bin and favorites table
        sqLiteDatabase.execSQL("CREATE TABLE bin (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "pictures_id INTEGER);");

        sqLiteDatabase.execSQL("CREATE TABLE favorites (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pictures_id INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Recreate table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS pictures");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS bin");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favorites");
        onCreate(sqLiteDatabase);
    }

    /**
     * Used to add pictures to the pictures table.
     * @param name
     * @param album
     */
    public void addPicture(String name, String album) {

    }

    /**
     * @param name: Name of the file from which it
     * has to determine the ID.
     * @return: The ID from the given name in the
     * pictures table.
     */
    public long getIdFromName(String name) {
        return 0;
    }

    /**
     * Add picture to either the bin or the favorites table
     * @param table: Picture add location.
     * @param id: Picture ID from the pictures table
     */
    public void addToList(String table, long id) {

    }

    /**
     * Delete pictures from the pictures table.
     * @param id: ID from the item that has to be
     * removed.
     */
    public void deletePicture(long id) {

    }

    /**
     * Delete picture from either the bin or the favorites table
     * @param table: Picture delete location
     * @param id: ID from the pictures table.
     */
    public void deleteFromList(String table, long id) {


    }
}
