package com.example.sander.pictureswipe;


import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;


/**
 * A DialogFragment that asks the user if he agrees to remove all the files that are currently
 * in the SQlite 'bin' table, from the device.
 */
public class ClearBinDialogFragment extends DialogFragment implements View.OnClickListener {

    private final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clear_bin_dialog, container, false);

        // Bind views and set listeners.
        Button accept = view.findViewById(R.id.accept);
        Button cancel = view.findViewById(R.id.cancel);
        accept.setOnClickListener(this);
        cancel.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept:
                clearBin();
                getDialog().dismiss();
                break;
            case R.id.cancel:
                getDialog().dismiss();
                break;
        }
    }


    /**
     * Function that removes every file from the SQlite 'bin' table. It asks permissions when
     * necessary and notifies the user if it has been successful.
     */
    public void clearBin() {
        // Handle write permission.
        GrandPermissions grandPermissions = new GrandPermissions();
        grandPermissions.checkWritePermission(getActivity(), REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);

        // Get an instance of the SQlite database
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        // Get cursor containing images from the bin
        Cursor cursor = db.selectAllBin("bin");

        // Get picture count
        int pictureCount = cursor.getCount();

        // Loop trough cursor and get the path
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String path = cursor.getString(cursor.getColumnIndex("path"));

                // Transform each path into a file
                File image = new File(path);

                // Remove the image using contentResolver to make sure it won't show up again and
                // the gallery rescans the folders.
                if (image.exists()) {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.ImageColumns.DATA + "=?", new String[]{path});
                }
                cursor.moveToNext();
            }
        }
        cursor.close();

        // Notify user
        Toast.makeText(getActivity(), "Removed " + String.valueOf(pictureCount)
                + " image(s) from your device.", Toast.LENGTH_SHORT).show();

        // Clear bin and reload BinFragment / Gallery.
        db.deleteAllFromList();
        ((MainActivity)getActivity()).reloadFragment(BinFragment.class.getName());
    }
}