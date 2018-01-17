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
    public void insertPicture(String name, String album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("album", album);

        db.insert("pictures", null, contentValues);
    }

    /**
     * @param name: Name of the file from which it
     * has to determine the ID.
     * @return: The ID from the given name in the
     * pictures table.
     */
    public long getIdFromName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pictures WHERE name='" + name + "';", null);
        int id_index = cursor.getColumnIndex("id");

        return cursor.getLong(id_index);
    }

    /**
     * Add picture to either the bin or the favorites table
     * @param table: Picture add location.
     * @param id: Picture ID from the pictures table
     */
    public void insertToList(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pictures_id", id);

        db.insert(table, null, contentValues);
    }

    public Boolean inPictures(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM pictures WHERE name='" + name + "';", null);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Delete pictures from the pictures table.
     * @param id: ID from the item that has to be
     * removed.
     */
    public void deletePicture(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM todo WHERE id=" + String.valueOf(id) + ";");
    }

    /**
     * Delete picture from either the bin or the favorites table
     * @param table: Picture delete location
     * @param id: ID from the pictures table.
     */
    public void deleteFromList(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table +  " WHERE id=" + String.valueOf(id) + ";");
    }
}
