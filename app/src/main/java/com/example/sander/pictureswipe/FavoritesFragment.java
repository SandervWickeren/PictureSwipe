package com.example.sander.pictureswipe;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass. Used to
 * show all favorites of the user in a ListView.
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

        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        GridView gridView = view.findViewById(R.id.favImageGrid);

        // Initiate PictureGridHandler;
        PictureGridHandler pictureGridHandler = new PictureGridHandler(getActivity(),
                "favorites", FavoritesFragment.class.getName());
        gridView.setOnItemClickListener(pictureGridHandler);
        gridView.setOnItemLongClickListener(pictureGridHandler);

        // Set adapter
        PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(getContext(), db.selectAllBin("favorites"));
        gridView.setAdapter(pictureGridAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate custom actionbar when logged in.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            inflater.inflate(R.menu.actionbar_favorites, menu);
        } else {
            inflater.inflate(R.menu.actionbar_standard, menu);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        String title = ((MainActivity) getActivity()).logText();
        menu.getItem(0).setTitle(title);
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


    public void syncWithCloud() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // Get firebasehelper instance
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.FirebaseHelper(getActivity());

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
        }
    }
}
