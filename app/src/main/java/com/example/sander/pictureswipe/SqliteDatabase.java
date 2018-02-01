package com.example.sander.pictureswipe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        "name TEXT, album TEXT, path TEXT);");

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
     * Function that returns a cursor containing information from the pictures from the
     * selected table.
     * @param table you want the images from (bin / favorites )
     * @return cursor with the output of the query.
     */
    public Cursor selectAllList(String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id AS _id, * FROM pictures WHERE id IN (" +
                "SELECT pictures_id FROM " + table +  ");", null);

        return cursor;
    }


    /**
     * Used to add a picture to the pictures table.
     * @param name of the picture.
     * @param album of the picture.
     * @param path of the picture.
     */
    public void insertPicture(String name, String album, String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Add values tot contentvalues.
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("album", album);
        contentValues.put("path", path);

        // Insert values to database.
        db.insert("pictures", null, contentValues);
    }


    /**
     * Deletes item from the pictures table if it's not a favorite picture.
     * @param name of a picture from the album.
     */
    public void deleteAlbumFromPictures(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get the id from the name
        long id = getIdFromName(name);

        // Get the album name
        Cursor cursor = db.rawQuery("SELECT album FROM pictures WHERE id=" + id + ";", null);
        cursor.moveToFirst();
        String album = cursor.getString(0);
        cursor.close();

        // Delete all pictures that have a certain album and are not in favorites table.
        db.execSQL("DELETE FROM pictures WHERE album='" + album + "' AND id" +
                " NOT IN (SELECT pictures_id FROM favorites);");
    }


    /**
     * Delete item from the pictures table
     * @param id you want to remove from the pictures table.
     */
    public void deleteFromPictures(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM pictures WHERE id='" + id + "';");
    }


    /**
     * @param name: Name of the file from which it
     * has to determine the ID.
     * @return The ID from the given name in the
     * pictures table.
     */
    public long getIdFromName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Execute query
        Cursor cursor = db.rawQuery("SELECT id FROM pictures WHERE name='" + name + "';", null);
        cursor.moveToFirst();
        Integer id = cursor.getInt(0);
        cursor.close();
        return id;
    }


    /**
     * Add picture to either the bin or the favorites table
     * @param table: Picture add location.
     * @param id: Picture ID from the pictures table
     */
    public void insertToList(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Add to contentvalues.
        ContentValues contentValues = new ContentValues();
        contentValues.put("pictures_id", id);

        db.insert(table, null, contentValues);
    }


    /**
     * Remove picture from either the bin or the favorites table
     * @param table picture remove location
     * @param id picture id from the pictures table.
     */
    public void deleteFromList(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table + " WHERE pictures_id='" + id + "';");
    }


    /**
     * Checks if image already in the pictures table.
     * @param name of the image.
     * @return either true of false if it's already in the table.
     */
    public Boolean inPictures(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Execute query.
        Cursor cursor = db.rawQuery("SELECT * FROM pictures WHERE name='" + name + "';", null);

        // Return accordingly
        if (cursor.getCount() == 0) {
            cursor.close();
            return Boolean.FALSE;
        } else {
            cursor.close();
            return Boolean.TRUE;
        }
    }


    /**
     * Delete picture from either the bin table.
     */
    public void deleteAllFromList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM bin;");
    }
}