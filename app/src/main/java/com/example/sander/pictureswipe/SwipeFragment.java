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

            LoadImages loadImages = new LoadImages();
            File[] images = loadImages.getList(getContext(), pictureUri);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }








}
