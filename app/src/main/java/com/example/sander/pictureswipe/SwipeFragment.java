package com.example.sander.pictureswipe;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SwipeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = getView().findViewById(R.id.content);

        try {
            Bundle bundle = this.getArguments();
            Uri pictureUri = bundle.getParcelable("uri");

            Picasso.with(getContext()).load(pictureUri).into(imageView);

            // Get path

            String path = getRealPathFromUri(getContext(), pictureUri);
            Log.d("testsss", path);
            Toast.makeText(getActivity(), path, Toast.LENGTH_SHORT).show();
            getImages(path);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Used to convert URI to a real path.
     * Source: https://stackoverflow.com/a/20059657
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void getImages(String path) {
        String[] splitted = path.split("/");

        String s = "";
        for (int i = 0; i < splitted.length - 1; i++) {
            s += splitted[i] + "/";
        }
        System.out.println(s);


       /* // Test
        File testfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        File[] listtestfiles = testfile.listFiles();
        for (File pf: listtestfiles) {
            System.out.println(pf.toString());
        }*/

        File f = new File(s);
        if (!f.exists() || !f.isDirectory()){
            System.out.println("No such directory");
        } else if (!f.canRead()) {
            System.out.println("Can't read");
        }

        File[] paths = f.listFiles();


        for (File p:paths){
            System.out.println(p.toString());
        }


    }



}
