package com.example.sander.pictureswipe;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


/**
 * Fragment that shows all the items from the SQlite 'bin' table in a GridView.
 */
public class BinFragment extends Fragment {

    PictureGridAdapter pictureGridAdapter;
    PictureGridHandler pictureGridHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Allows the fragment to use it's own actionbar.
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_bin, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get database instance
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        // Bind view
        GridView gridView = view.findViewById(R.id.binImageGrid);

        // Instance of the class containing all listeners.
        pictureGridHandler = new PictureGridHandler(getActivity(), "bin",
                BinFragment.class.getName());

        // Bind class to all clickListener types.
        gridView.setOnItemClickListener(pictureGridHandler);
        gridView.setOnItemLongClickListener(pictureGridHandler);

        // Bind adapter.
        pictureGridAdapter = new PictureGridAdapter(getContext(), db.selectAllList("bin"));
        gridView.setAdapter(pictureGridAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

        // When the user logged in and resumes it redraws the ActionBar an forces to update the
        // title.
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate custom actionbar.
        inflater.inflate(R.menu.actionbar_menu, menu);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Find out if ActionBar text should be 'login' or 'logout'.
        String title = ((MainActivity)getActivity()).logText();
        menu.getItem(1).setTitle(title);
    }


    /**
     * Handles the ActionBar button clicks.
     * @param item that has been clicked.
     * @return always true.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAll:
                deleteAllFromDevice();
                break;
            case R.id.logStatus:
                ((MainActivity)getActivity()).logAction();
                onResume();
                break;
        }
        return true;
    }


    /**
     * Function that launches a DialogFragment to ask the user if he wants to delete the pictures
     * from his device.
     */
    public void deleteAllFromDevice() {
        // Final user-check using DialogFragment
        ClearBinDialogFragment fragment = new ClearBinDialogFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.show(ft, "dialog");
    }
}