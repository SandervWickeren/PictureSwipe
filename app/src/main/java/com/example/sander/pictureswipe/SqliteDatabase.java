package com.example.sander.pictureswipe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.database.DatabaseUtils.dumpCursorToString;

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

        // Add some test values
        sqLiteDatabase.execSQL("INSERT INTO pictures (name, album, path) VALUES ('Naam 1', 'Album 1', '/Storage/no/clue');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Recreate table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS pictures");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS bin");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favorites");
        onCreate(sqLiteDatabase);
    }

    public Cursor selectAllPictures(String table) {
        System.out.println("selectAllPictures is being runned.");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id AS _id, * FROM " + table + ";", null);

        System.out.println(cursor.getCount());

        return cursor;
    }

    public Cursor selectAllBin(String table) {
        System.out.println("selectAllBin is being runned.");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id AS _id, * FROM pictures WHERE id IN (" +
                "SELECT pictures_id FROM " + table +  ");", null);
        Log.v("selectAllBin", dumpCursorToString(cursor));

        System.out.println(cursor.getCount());

        return cursor;
    }

    /**
     * Used to add pictures to the pictures table.
     * @param name
     * @param album
     */
    public void insertPicture(String name, String album, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("album", album);
        contentValues.put("path", path);

        long result = db.insert("pictures", null, contentValues);
        if (result == -1) {
            System.out.println("Couldn't add to db.");
        } else {
            System.out.println(result);
        }
    }

    /**
     * @param name: Name of the file from which it
     * has to determine the ID.
     * @return: The ID from the given name in the
     * pictures table.
     */
    public long getIdFromName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM pictures WHERE name='" + name + "';", null);
        Log.v("desc", dumpCursorToString(cursor));
        cursor.moveToFirst();
        Integer id = cursor.getInt(0);
        Log.d("id", id.toString());

        return id;
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

    /**
     * Checks if image already in pictures
     * @param name
     * @return
     */
    public Boolean inPictures(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM pictures WHERE name='" + name + "';", null);

        if (cursor.getCount() == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * Delete pictures from the pictures table.
     * removed.
     */
    public void deleteAllPictures() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM pictures;");
    }

    /**
     * Delete picture from either the bin or the favorites table
     */
    public void deleteAllFromList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM bin;");
    }

    public void deepDelete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("pictures", null, null);
        db.delete("bin", null, null);
        db.delete("favorites", null, null);

    }
}
