package com.example.sander.pictureswipe;

import android.content.Context;

/**
 * This class handles the instance of the SqliteDatabase and creates
 * a new instance if necessary.
 */
public class SqliteDatabaseSingleton {

    private static SqliteDatabase instance;

    private SqliteDatabaseSingleton() {}

    public static SqliteDatabase getInstance(Context context) {

        // Only if there is no instance, it 'll create a new one.
        if (instance == null) {
            instance = new SqliteDatabase(context, "application", null, 1);
        }
        return instance;
    }
}