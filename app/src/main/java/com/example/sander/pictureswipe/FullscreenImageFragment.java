package com.example.sander.pictureswipe;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.io.File;


/**
 * DialogFragment that is used to show an image full screen.
 */
public class FullscreenImageFragment extends DialogFragment implements View.OnClickListener{

    ImageView imageFullscreen, close;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);

        // Bind views and set listeners.
        imageFullscreen = view.findViewById(R.id.imageFullscreen);
        close = view.findViewById(R.id.close);
        close.setOnClickListener(this);

        // Get the image path from the bundle.
        Bundle bundle = this.getArguments();
        String path = bundle.getString("path");
        File image = new File(path);

        // Load image into ImageView using picasso.
        Picasso.with(getContext())
                .load(image)
                .placeholder(R.drawable.ic_wallpaper_black_24dp)
                .resize(0, 1500)
                .into(imageFullscreen);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                getDialog().dismiss();
                break;
        }
    }
}