package com.example.sander.pictureswipe;


import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClearBinDialogFragment extends DialogFragment implements View.OnClickListener {

    private final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clear_bin_dialog, container, false);

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
                getDialog().cancel();
                break;
            case R.id.cancel:
                getDialog().cancel();
                break;
        }
    }

    public void clearBin() {
        // Handle write permission.
        GrandPermissions grandPermissions = new GrandPermissions();
        grandPermissions.checkWritePermission(getActivity(), REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);

        // Get an instance of the sqlite database
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        // Get cursor containing images from the bin
        Cursor cursor = db.selectAllBin("bin");

        // Get picture count
        int pictureCount = cursor.getCount();

        // Loop trough cursor and get the path
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String path = cursor.getString(cursor.getColumnIndex("path"));

                System.out.println("File: " + path);

                // Transform each path into a file
                File image = new File(path);

                // Remove the file
                if (image.exists()) {
                    boolean deleted = image.delete();
                }
                cursor.moveToNext();
            }
        }
        cursor.close();

        // Notify user
        Toast.makeText(getActivity(), "Removed " + String.valueOf(pictureCount)
                + "images", Toast.LENGTH_SHORT).show();

        // Clear bin and reload BinFragment / Gallery.
        db.deleteAllFromList();
        reloadBinAndGallery();

    }

    public void reloadBinAndGallery() {

        // Get BinFragment from manager
        Fragment fragment = getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(BinFragment.class.getName());

        // Reload the fragment
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(fragment);
        ft.attach(fragment);
        ft.commit();

        // Reload gallery
        MediaScannerConnection.scanFile(getActivity(),
                new String[]{Environment.getExternalStorageDirectory().toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {

            }
        });
    }

}
