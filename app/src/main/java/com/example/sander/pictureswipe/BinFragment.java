package com.example.sander.pictureswipe;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass. Used to
 * show all pictures placed in the bin in a ListView.
 */
public class BinFragment extends Fragment {

    PictureGridAdapter pictureGridAdapter;

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

        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        GridView gridView = view.findViewById(R.id.binImageGrid);
        gridView.setOnItemClickListener(new GridListener());
        gridView.setOnItemLongClickListener(new GridLongListener());
        PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(getContext(), db.selectAllBin("bin"));
        gridView.setAdapter(pictureGridAdapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate custom actionbar.
        inflater.inflate(R.menu.actionbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.deleteAll) {
            deleteAllFromDevice();
        }

        return true;
    }

    public void deleteAllFromDevice() {

        // Final user-check using DialogFragment
        ClearBinDialogFragment fragment = new ClearBinDialogFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.show(ft, "dialog");

    }



    private class GridListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            // Get the cursor from the adapter
            Cursor cursor = ((PictureGridAdapter)adapterView.getAdapter()).getCursor();

            // Move it to the correct position
            cursor.moveToPosition(position);

            // Retrieve the path
            String path = cursor.getString(cursor.getColumnIndex("path"));

            // Launch new fragment using the path
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            FullscreenImageFragment fragment = new FullscreenImageFragment();
            fragment.setArguments(bundle);
            //((MainActivity)getActivity()).replaceFragment(fragment);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            fragment.show(ft, "dialog");


        }
    }
    public void snackbarHandler(final String name, final Integer id, final String table) {

        // Remove item from table
        final SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());
        db.deleteFromList(table, id);

        // Reload the GridView.
        ((MainActivity)getActivity()).reloadFragment(BinFragment.class.getName());

        // Create info string.
        String info = "You removed " + name;

        // Create information snackbar with undo option. When the user presses the undo
        // button, the image 'll be restored.
        Snackbar delete = Snackbar
                .make(getActivity().findViewById(R.id.snackbarLocation), info,
                        Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Undo has been clicked and the image 'll be restored
                        db.insertToList(table, id);

                        // Reload the GridView.
                        ((MainActivity)getActivity()).reloadFragment(BinFragment.class.getName());

                        // Notify user that it has been restored
                        Snackbar undo = Snackbar.make(getActivity().findViewById(R.id.snackbarLocation),
                                "Succesfully restored the image!",
                                Snackbar.LENGTH_SHORT);
                        undo.show();
                    }
                });

        delete.show();

    }

    private class GridLongListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            System.out.println("LongClick");

            // Get the cursor from the adapter
            Cursor cursor = ((PictureGridAdapter)adapterView.getAdapter()).getCursor();

            // Move it to the correct position
            cursor.moveToPosition(position);

            // Retrieve name and id from the selected image.
            final String name = cursor.getString(cursor.getColumnIndex("name"));
            Integer id = cursor.getInt(cursor.getColumnIndex("id"));
            System.out.println(id);

            // Sent info to the SnackBarHandler
            snackbarHandler(name, id, "bin");

            return true;
        }
    }
}
