package com.example.sander.pictureswipe;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
    public Snackbar delete;

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
        DialogFragment fragment = new FullscreenImageFragment();
        ((MainActivity)mContext).launchDialog(fragment, path);
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
        String path = cursor.getString(cursor.getColumnIndex("path"));
        System.out.println(id);

        // Sent info to the SnackBarHandler
        snackbarHandler(name, id, table, path);

        return true;
    }

    public void snackbarHandler(final String name, final Integer id, final String table, final String path) {

        // Remove item from table
        final SqliteDatabase db = SqliteDatabaseSingleton.getInstance(mContext.getApplicationContext());
        db.deleteFromList(table, id);

        // Reload the GridView.
        ((MainActivity)mContext).reloadFragment(fragmentTag);

        // Create info string.
        String info = "Removed the image from your " + table;

        // Create information snackbar with undo option. When the user presses the undo
        // button, the image 'll be restored.
        delete = Snackbar
                .make(v.findViewById(R.id.coordinatorLayout), info,
                        Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Undo has been clicked and the image 'll be restored
                        db.insertToList(table, id);

                        // Reload the GridView only if the user hasn't already gone to another
                        // fragment.
                        if (activeFragment(fragmentTag)) {
                            ((MainActivity)mContext).reloadFragment(fragmentTag);
                        }

                        // Notify user that it has been restored
                        Snackbar undo = Snackbar.make(v.findViewById(R.id.snackbarLocation),
                                "Succesfully restored the image!",
                                Snackbar.LENGTH_SHORT);
                        undo.show();
                    }
                }).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        // If the user hasn't pressed 'UNDO' and the SnackBar is timed out
                        // remove the picture from FireBase (to minimize bandwidth).
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {

                            FirebaseHelper firebaseHelper = new FirebaseHelper();
                            firebaseHelper.FirebaseHelper(mContext);
                            firebaseHelper.removeFile(name, path);
                           /* FavoritesFragment fragment = new FavoritesFragment();
                            fragment.removeFile(name, path);*/

                            // Finally remove also from picture database
                            db.deleteFromPictures(id);
                        }
                    }
                });

        delete.show();

    }

    /**
     * Function that checks whether the current fragment is active or not.
     * @param tag of the fragment you want to check for.
     * @return Either True or False depending on if its active or not.
     */
    private Boolean activeFragment(String tag) {
        Fragment currentFragment = ((MainActivity) mContext)
                .getSupportFragmentManager().findFragmentByTag(tag);

        if (currentFragment != null && currentFragment.isVisible()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
