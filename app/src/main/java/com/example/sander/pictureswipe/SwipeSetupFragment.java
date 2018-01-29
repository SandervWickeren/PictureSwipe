package com.example.sander.pictureswipe;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass. Used to setup the start
 * of the Swipe fragment, using the selected properties.
 */
public class SwipeSetupFragment extends Fragment implements View.OnClickListener {

    public static final int GALLERY_REQUEST = 10;
    private final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swipe_setup, container, false);

        // Allows the fragment to use it's own actionbar.
        setHasOptionsMenu(true);

        ImageView selectAlbum = view.findViewById(R.id.addAlbum);
        selectAlbum.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GrandPermissions grandPermissions = new GrandPermissions();
        grandPermissions.checkReadPermission(getActivity(), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_standard, menu);
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
            case R.id.logStatus:
                ((MainActivity)getActivity()).logAction();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addAlbum:
                selectGalleryImage(view);
                break;
        }
    }

    /**
     * This method 'll be invoked when the user clicks the 'select' button. The selected
     * picture 'll be processed in the MainActivity at 'onActivityResult'.
     * @param view
     */
    public void selectGalleryImage(View view) {

        // Launch image gallery using an implicit intent.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);

        // Get the filepath of the location of the images.
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picturePath = pictureDirectory.getPath();
        Uri data = Uri.parse(picturePath);

        // Set the correct image filetype.
        galleryIntent.setDataAndType(data, "image/*");

        // Use request code to trace back the result
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }

    /**
     * This method handles the response from the gallery activity. It gets called
     * from the MainActivity and starts the SwipeFragment.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri pictureUri = data.getData();

            Toast.makeText(getActivity(), pictureUri.getPath(), Toast.LENGTH_SHORT).show();

            // Load new fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("uri", pictureUri);
            SwipeFragment fragment = new SwipeFragment();
            fragment.setArguments(bundle);
            ((MainActivity)getActivity()).replaceFragment(fragment);

        } else {
            // TODO show error
        }

    }
}
