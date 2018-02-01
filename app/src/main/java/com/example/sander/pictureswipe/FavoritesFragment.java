package com.example.sander.pictureswipe;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.google.firebase.auth.FirebaseAuth;



/**
 * Fragment that shows all the items from the SQlite 'favorites' table in a GridView.
 */
public class FavoritesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Allows the fragment to use it's own actionbar.
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get database instance
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        // Bind view.
        GridView gridView = view.findViewById(R.id.favImageGrid);

        // Instance of the class containing all listeners.
        PictureGridHandler pictureGridHandler = new PictureGridHandler(getActivity(),
                "favorites", FavoritesFragment.class.getName());

        // Bind class to all clickListener types.
        gridView.setOnItemClickListener(pictureGridHandler);
        gridView.setOnItemLongClickListener(pictureGridHandler);

        // Bind adapter.
        PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(getContext(), db.selectAllBin("favorites"));
        gridView.setAdapter(pictureGridAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

        // Force ActionBar redrawing to show the correct 'login' / 'logout' title.
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the correct ActionBar based on login status.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            inflater.inflate(R.menu.actionbar_favorites, menu);
        } else {
            inflater.inflate(R.menu.actionbar_standard, menu);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Find out if ActionBar text should be 'login' or 'logout'.
        String title = ((MainActivity) getActivity()).logText();
        menu.getItem(1).setTitle(title);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.syncAll:
                syncWithCloud();
                break;
            case R.id.logStatus:
                ((MainActivity) getActivity()).logAction();
                onResume();
                break;
        }
        return true;
    }


    /**
     * Function that handles the cloud syncing. It checks if the user is logged in and using
     * the FirebaseHelper class it up- and downloads the correct images.
     */
    public void syncWithCloud() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // Get FirebaseHelper instance and give it the context.
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.FirebaseHelper(getActivity());

            firebaseHelper.launchMessage("Started syncing");

            // Get the database cursor
            SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());
            Cursor cursor = db.selectAllBin("favorites");

            // Check for every favorite if an upload is needed
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    // Retrieve the path
                    String path = cursor.getString(cursor.getColumnIndex("path"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));

                    // Handle upload
                    firebaseHelper.inCloudStorage(name, path);

                    // Go to next favorite
                    cursor.moveToNext();
                }
            }
            cursor.close();

            // Finally download items that are not available locally
            firebaseHelper.inLocalStorage();
            firebaseHelper.launchMessage("Done syncing");
        }
    }
}