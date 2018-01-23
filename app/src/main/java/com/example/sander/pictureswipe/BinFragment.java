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
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass. Used to
 * show all pictures placed in the bin in a ListView.
 */
public class BinFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        GridView gridView = view.findViewById(R.id.binImageGrid);
        gridView.setOnItemClickListener(new GridListener());
        PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(getContext(), db.selectAllBin("bin"));
        gridView.setAdapter(pictureGridAdapter);


        test(db.selectAllBin("bin"));
    }

    public void test(Cursor bin) {
        List<String> binImages = new ArrayList<>();

        if (bin.moveToFirst()) {
            while (!bin.isAfterLast()) {
                String data = bin.getString(bin.getColumnIndex("name"));
                System.out.println("FROM DATABASE: " + data);
                bin.moveToNext();
            }
        }
        bin.close();
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
}
