package com.example.sander.pictureswipe;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class FullscreenImageFragment extends DialogFragment {

    ImageView imageFullscreen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);

        imageFullscreen = view.findViewById(R.id.imageFullscreen);
        Bundle bundle = this.getArguments();
        String path = bundle.getString("path");
        File image = new File(path);

        Picasso.with(getContext())
                .load(image)
                .placeholder(R.drawable.ic_wallpaper_black_24dp)
                .resize(0, 1500)
                .into(imageFullscreen);

        return view;
    }

}
