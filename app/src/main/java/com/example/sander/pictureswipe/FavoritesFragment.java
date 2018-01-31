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

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uid;

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

        getReferences();

        test(db.selectAllPictures("pictures"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        getReferences();
    }

    public void getReferences() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }
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

        String title = ((MainActivity)getActivity()).logText();
        menu.getItem(0).setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.syncAll:
                syncWithCloud();
                break;
            case R.id.logStatus:
                ((MainActivity)getActivity()).logAction();
                onResume();
                break;
        }
        return true;
    }

    public void refresh() {
        Fragment fragment = getFragmentManager().findFragmentByTag(FavoritesFragment.class.getName());
        if (fragment != null && fragment.isVisible()) {
            ((MainActivity)getActivity()).reloadFragment(FavoritesFragment.class.getName());
        }
    }

    public void syncWithCloud() {
        if (mAuth.getCurrentUser() != null) {

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

            // Finally download items that are not available locally
            downloadFiles();

        }

    }

    public void inStorage(final String name, final String path) {
        mStorageRef.child(uid + "/" + name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    public void downloadFiles() {

        // Query the database for all the favorites
        Query query = mDatabase.child(uid).child("images");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Stores the files that have to be downloaded
                ArrayList<FirebaseImage> downloadList = new ArrayList<>();

                // Loop trough every images
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    FirebaseImage firebaseImage = postSnapshot.getValue(FirebaseImage.class);

                    final File img = new File(URI.create(firebaseImage.uri).getPath());

                    if (img.exists()) {
                        System.out.println(URI.create(firebaseImage.uri).getPath() + " exists");

                    } else {
                        System.out.println(firebaseImage.uri + " doesn't exists");

                        // Get a file reference from FirebaseStorage.
                        StorageReference fileRef = FirebaseStorage.getInstance().
                                getReferenceFromUrl(firebaseImage.downloadUrl);

                        // Start downloading the file
                        fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Create new file using the saved path
                                try {

                                    // Download the file
                                    img.createNewFile();

                                    FileOutputStream stream = new FileOutputStream(img.getPath());
                                    stream.write(bytes);
                                    System.out.println("Downloaded images to: " + img.getPath());

                                    // Update the MediaScanner
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Images.Media.DATA, img.getAbsolutePath());
                                    getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                    // Add to favorites
                                    SqliteDatabaseHelper dbHelper = new SqliteDatabaseHelper(getActivity());
                                    dbHelper.addToList(img.getAbsolutePath(), "favorites");

                                    // Refresh
                                    refresh();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    public void removeFile(final String name, final String path) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            // Create a StorageReference to the file to delete
            StorageReference desertRef = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + name);

            // Remove Image also from database
            deleteUploadReference(path);

            // Delete the file
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }

    }

    public void uploadFile(String name, String path) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            final Uri file = Uri.fromFile(new File(path));
            StorageReference riversRef = mStorageRef.child(uid  + "/" + name);

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            addUploadReference(file, downloadUrl.toString());
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

    }

    public void addUploadReference(final Uri uri, final String downloadUrl) {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Instance of FirebaseImage
                FirebaseImage image = new FirebaseImage(uri, downloadUrl);

                // Get a random valid key
                String newKey = mDatabase.child(uid).child("images").push().getKey();

                // Push values to Firebase
                mDatabase.child(uid).child("images").child(newKey).setValue(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteUploadReference(String path) {

        // Get uri
        final Uri uri = Uri.fromFile(new File(path));

        System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Query the database for all the favorites
        Query query = mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("images");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {

                    System.out.println("Snapshot child: " + keySnapshot.child("uri").getValue());
                    System.out.println("uri value: " + uri.toString());
                    if (Objects.equals(keySnapshot.child("uri").getValue(), uri.toString())) {
                        keySnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
