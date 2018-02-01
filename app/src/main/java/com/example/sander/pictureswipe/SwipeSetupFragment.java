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
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.File;
import static android.app.Activity.RESULT_OK;


/**
 * Used to setup the start of the Swipe fragment, using the selected properties.
 */
public class SwipeSetupFragment extends Fragment implements View.OnClickListener {

    // Codes necessary for requests.
    public static final int GALLERY_REQUEST = 10;
    private final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swipe_setup, container, false);

        // Allows the fragment to use it's own actionbar.
        setHasOptionsMenu(true);

        // Bind view and listener
        ImageButton selectAlbum = view.findViewById(R.id.addAlbum);
        selectAlbum.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if it has the permissions to read external storage.
        GrandPermissions grandPermissions = new GrandPermissions();
        grandPermissions.checkReadPermission(getActivity(), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Reload ActionBar
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // No permission, the app cannot be used so it 'll be closed.
                    getActivity().finish();
                }
                return;
            }
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

        // Find out if ActionBar text should be 'login' or 'logout'.
        String title = ((MainActivity)getActivity()).logText();
        menu.getItem(1).setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logStatus:
                ((MainActivity)getActivity()).logAction();
                onResume();
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
     * This method 'll be invoked when the user clicks the 'add' button. The selected
     * picture 'll be processed in the MainActivity at 'onActivityResult'.
     * @param view current view.
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
     * @param requestCode Code used to launch the gallery activity.
     * @param resultCode Code that gives if it was successful or not.
     * @param data Retrieved from the activity (the selected image).
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri pictureUri = data.getData();

            // Load new fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("uri", pictureUri);
            SwipeFragment fragment = new SwipeFragment();
            fragment.setArguments(bundle);
            ((MainActivity)getActivity()).replaceFragment(fragment);

        } else {
            Toast.makeText(getContext(), "Couldn't load the images", Toast.LENGTH_SHORT).show();
        }

    }
}