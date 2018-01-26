package com.example.sander.pictureswipe;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by sander on 25-1-2018.
 */

public class PictureGridHandler implements GridView.OnItemClickListener, GridView.OnItemLongClickListener {

    private Context mContext;
    private View v;
    private String table;
    private String fragmentTag;

    public PictureGridHandler(Context context, String table, String fragmentTag) {
        this.mContext = context;
        this.table = table;
        this.v = ((MainActivity)context).getWindow().getDecorView();
        this.fragmentTag = fragmentTag;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        // Get the cursor from the adapter
        Cursor cursor = ((PictureGridAdapter)adapterView.getAdapter()).getCursor();

        // Move it to the correct position
        cursor.moveToPosition(position);

        // Retrieve the path
        String path = cursor.getString(cursor.getColumnIndex("path"));

        // Launch new fragment using the path
        ((MainActivity)mContext).launchImageDialog(path);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        System.out.println("LongClick");

        // Get the cursor from the adapter
        Cursor cursor = ((PictureGridAdapter)adapterView.getAdapter()).getCursor();

        // Move it to the correct position
        cursor.moveToPosition(position);

        // Retrieve name and id from the selected image.
        final String name = cursor.getString(cursor.getColumnIndex("name"));
        Integer id = cursor.getInt(cursor.getColumnIndex("id"));
        System.out.println(id);

        // Sent info to the SnackBarHandler
        snackbarHandler(name, id, table);

        return true;
    }

    public void snackbarHandler(final String name, final Integer id, final String table) {

        // Remove item from table
        final SqliteDatabase db = SqliteDatabaseSingleton.getInstance(mContext.getApplicationContext());
        db.deleteFromList(table, id);

        // Reload the GridView.
        ((MainActivity)mContext).reloadFragment(fragmentTag);

        // Create info string.
        String info = "You removed " + name;

        // Create information snackbar with undo option. When the user presses the undo
        // button, the image 'll be restored.
        Snackbar delete = Snackbar
                .make(v.findViewById(R.id.snackbarLocation), info,
                        Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Undo has been clicked and the image 'll be restored
                        db.insertToList(table, id);

                        // Reload the GridView.
                        ((MainActivity)mContext).reloadFragment(fragmentTag);

                        // Notify user that it has been restored
                        Snackbar undo = Snackbar.make(v.findViewById(R.id.snackbarLocation),
                                "Succesfully restored the image!",
                                Snackbar.LENGTH_SHORT);
                        undo.show();
                    }
                });

        delete.show();

    }
}