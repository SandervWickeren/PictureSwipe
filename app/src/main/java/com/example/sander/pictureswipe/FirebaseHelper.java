package com.example.sander.pictureswipe;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by sander on 31-1-2018.v
 */

public class FirebaseHelper {

    private Context context;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uid;

    public void FirebaseHelper(Context context) {
        this.context = context;
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            this.uid = mAuth.getCurrentUser().getUid();
        }

    }


    public void inCloudStorage(final String name, final String path) {
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


    public void uploadFile(String name, String path) {

        final Uri file = Uri.fromFile(new File(path));
        StorageReference imgRef = mStorageRef.child(uid  + "/" + name);

        imgRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        addUploadReference(file, downloadUrl.toString());
                        Toast.makeText(context, "Succesfully uploaded to the cloud", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // launch error
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                    }
                });
        launchMessage("Done with uploading");
    }


    public void inLocalStorage() {

        // Query the database for all the favorites
        Query query = mDatabase.child(uid).child("images");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Loop trough every images
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FirebaseImage firebaseImage = postSnapshot.getValue(FirebaseImage.class);

                    final File img = new File(URI.create(firebaseImage.uri).getPath());


                    if (img.exists()) {
                        // No action required
                        System.out.println(URI.create(firebaseImage.uri).getPath() + " exists");
                    } else {
                        // Download from cloud
                        downloadFiles(img, firebaseImage);
                        System.out.println(firebaseImage.uri + " doesn't exists");



                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        launchMessage("Done syncing");
    }


    public void downloadFiles(final File img, FirebaseImage firebaseImage) {

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
                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    // Add to favorites
                    SqliteDatabaseHelper dbHelper = new SqliteDatabaseHelper(context);
                    dbHelper.addToList(img.getAbsolutePath(), "favorites");

                    // Refresh
                    ((MainActivity)context).reloadFragment(FavoritesFragment.class.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        launchMessage("Done with downloading");
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

        // Query the database for all the favorites
        Query query = mDatabase.child(uid).child("images");
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


    public void removeFile(final String name, final String path) {

            // Create a StorageReference to the file to delete
            StorageReference desertRef = mStorageRef.child(uid + "/" + name);

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


    public void launchMessage(String message) {
        View v = ((MainActivity)context).getWindow().getDecorView();
        Snackbar undo = Snackbar.make(v.findViewById(R.id.snackbarLocation), message,
                Snackbar.LENGTH_SHORT);
        undo.show();
    }






















}
