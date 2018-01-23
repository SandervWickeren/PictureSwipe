package com.example.sander.pictureswipe;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass. Used to
 * show all favorites of the user in a ListView.
 */
public class FavoritesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        GridView gridView = view.findViewById(R.id.favImageGrid);
        gridView.setOnItemClickListener(new RepeatGridListener());

        PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(getContext(), db.selectAllBin("favorites"));
        gridView.setAdapter(pictureGridAdapter);

        test(db.selectAllPictures("pictures"));

    }

    public void test(Cursor cursor) {
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String data = cursor.getString(cursor.getColumnIndex("name"));
                System.out.println("FROM DATABASE: " + data);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    private class RepeatGridListener implements AdapterView.OnItemClickListener {

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
}
