package com.example.sander.pictureswipe;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass. Used to
 * show all favorites of the user in a ListView.
 */
public class FavoritesFragment extends Fragment {

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

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

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        StorageReference imageReference = mStorageRef.child("favorites");

        test(db.selectAllPictures("pictures"));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.syncAll:
                syncWithCloud();
                break;
            case R.id.logStatus:
                ((MainActivity)getActivity()).launchLogin();
                break;
        }
        return true;
    }

    public void syncWithCloud() {

        // Get the database cursor
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());
        Cursor cursor = db.selectAllBin("favorites");

        // Check for every file if an upload is needed
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                // Retrieve the path
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                // Handle upload
                inStorage(name, path);

                cursor.moveToNext();
            }
        }
        cursor.close();


    }

    public void inStorage(final String name, final String path) {
        mStorageRef.child(mAuth.getCurrentUser().getUid() + "/" + name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // It's already in the cloud, so no upload needed.
                System.out.println(name + " is already in the cloud");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Not in the cloud, upload needed.
                uploadFile(name, path);
                System.out.println("Name: " + name + " and path: " + path);
            }
        });

    }

    public void downloadFile(String name, String path) {

    }

    public void removeFile(final String name) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Create a reference to the file to delete
        StorageReference desertRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "/" + name);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

    }

    public void uploadFile(String name, String path) {
        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "/" + name);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getActivity(), "Succesfully uploaded to the cloud", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                    }
                });

    }

    public void test(Cursor cursor) {
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String data = cursor.getString(cursor.getColumnIndex("name"));
                System.out.println("FROM DATABASE: " + data);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }
}
