package com.example.sander.pictureswipe;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swipe_setup, container, false);

        Button select = view.findViewById(R.id.select);
        select.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select:
                Toast.makeText(getActivity(), "Yes", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri pictureUri = data.getData();

            Toast.makeText(getActivity(), pictureUri.getPath(), Toast.LENGTH_SHORT).show();

            // Load new fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("uri", pictureUri);
            SwipeFragment fragmnent = new SwipeFragment();
            fragmnent.setArguments(bundle);
            ((MainActivity)getActivity()).replaceFragment(fragmnent);

        } else {
            // TODO show error
        }

    }
}
