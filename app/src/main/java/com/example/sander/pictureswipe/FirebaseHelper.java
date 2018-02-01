package com.example.sander.pictureswipe;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;


/**
 * This class contains all functions that are used in combination with Firebase. Storage, database
 * and authorisation are handled here.
 */
public class FirebaseHelper {

    private Context context;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uid;

    public void FirebaseHelper(Context context) {

        // Bind the variables.
        this.context = context;
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            this.uid = mAuth.getCurrentUser().getUid();
        }
    }


    /**
     * Function that checks if a given file is already in the cloud available. If it is not
     * it launches uploadFile() to upload it to the cloud.
     * @param name of the image.
     * @param path of the image.
     */
    public void inCloudStorage(final String name, final String path) {
        mStorageRef.child(uid + "/" + name)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // It's already in the cloud, so no action required.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Not in the cloud, upload needed.
                uploadFile(name, path);
            }
        });
    }


    /**
     * Function that tries to upload a given image to Firebase storage.
     * @param name of the image.
     * @param path of the image.
     */
    public void uploadFile(String name, String path) {
        // Transform path to Uri.
        final Uri file = Uri.fromFile(new File(path));

        // Create reference for Firebase storage.
        StorageReference imgRef = mStorageRef.child(uid  + "/" + name);

        // Start upload process.
        imgRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get a download URL to the uploaded content and add reference to the
                        // Firebase database.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        addUploadReference(file, downloadUrl.toString());

                        // Notify user.
                        launchMessage("Syncing..");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        launchMessage("Error while uploading, please retry syncing.");
                    }
                });
    }


    /**
     * Function that checks if a given file is already in the local storage. If it is not
     * it launches downloadFiles() to download it to the local storage.
     */
    public void inLocalStorage() {
        // Query Firebase for all the favorites
        Query query = mDatabase.child(uid).child("images");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop trough every images
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Catch value in the FirebaseImage class.
                    FirebaseImage firebaseImage = postSnapshot.getValue(FirebaseImage.class);

                    // Bind path to file.
                    final File img = new File(URI.create(firebaseImage.uri).getPath());

                    // Check if image exists or not.
                    if (img.exists()) {
                        // No action required
                    } else {
                        // Download image from cloud
                        downloadFiles(img, firebaseImage);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // No feedback for the user
            }
        });
    }


    /**
     * Function that tries to download the given file to the local storage.
     * @param img is a reference to the file.
     * @param firebaseImage instance of the FirebaseImage class.
     */
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
                    // Create file to download to.
                    img.createNewFile();

                    // Start writing bytes to the file.
                    FileOutputStream stream = new FileOutputStream(img.getPath());
                    stream.write(bytes);

                    // Update the MediaScanner, so it's visible for the gallery.
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, img.getAbsolutePath());
                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    // Add to downloaded image to favorites.
                    SqliteDatabaseHelper dbHelper = new SqliteDatabaseHelper(context);
                    dbHelper.addToList(img.getAbsolutePath(), "favorites");

                    // Refresh
                    ((MainActivity)context).reloadFragment(FavoritesFragment.class.getName());

                    launchMessage("Syncing..");

                } catch (IOException e) {
                    launchMessage("Error while downloading, please try again.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                launchMessage("Error while downloading, please try again.");
            }
        });
    }


    /**
     * Adds a reference containing the Uri and the Firebase storage download link into the
     * Firebase database, so it can easily be retrieved on another device.
     * @param uri of the image that needs a reference.
     * @param downloadUrl of the image from the Firebase storage.
     */
    public void addUploadReference(final Uri uri, final String downloadUrl) {
        // Push reference to Firebase.
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
                launchMessage("Couldn't create a reference, please try again.");
            }
        });
    }


    /**
     * Removes a reference from the Firebase database.
     * @param path from the image.
     */
    public void deleteUploadReference(String path) {
        // Get uri
        final Uri uri = Uri.fromFile(new File(path));

        // Query the database for all the favorites
        Query query = mDatabase.child(uid).child("images");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop trough all favorites of the user.
                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {

                    // If the favorite is equal to the reference you want te remove, remove value.
                    if (Objects.equals(keySnapshot.child("uri").getValue(), uri.toString())) {
                        keySnapshot.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                launchMessage("Couldn't remove the reference, please try again.");
            }
        });
    }


    /**
     * Removes the image from Firebase storage, and calls deleteUploadReference(path) to also
     * remove the reference.
     * @param name of the image.
     * @param path of the image.
     */
    public void removeFile(final String name, final String path) {
            // Create a StorageReference to the file to delete
            StorageReference desertRef = mStorageRef.child(uid + "/" + name);

            // Delete the file from Firebase Storage
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Remove image also from the Firebase database
                    deleteUploadReference(path);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // No feedback
                }
            });
    }


    /**
     * Function that launches a SnackBar, containing the given message.
     * @param message String containing the message.
     */
    public void launchMessage(String message) {
        View v = ((MainActivity)context).getWindow().getDecorView();
        Snackbar undo = Snackbar.make(v.findViewById(R.id.snackbarLocation), message,
                Snackbar.LENGTH_SHORT);
        undo.show();
    }
}