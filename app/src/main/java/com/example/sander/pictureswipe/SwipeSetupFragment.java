package com.example.sander.pictureswipe;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;


/**
 * A simple {@link Fragment} subclass. Used to setup the start
 * of the Swipe fragment, using the selected properties.
 */
public class SwipeSetupFragment extends Fragment implements View.OnClickListener {

    public static final int GALLERY_REQUEST = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swipe_setup, container, false);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select:
                selectGalleryImage(view);
                break;
            case R.id.start:
                break;
        }
    }

    /**
     * This method 'll be invoked when the user clicks the 'select' button
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

        startActivityForResult(galleryIntent, GALLERY_REQUEST);


    }

}
