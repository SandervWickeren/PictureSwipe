package com.example.sander.pictureswipe;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * ... TODO
 */

public class PictureGridAdapter extends ResourceCursorAdapter {


    public PictureGridAdapter(Context context, Cursor c) {
        super(context, R.layout.image_grid_layout, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        System.out.println("Bindviewing grid");
        ImageView gridImage = view.findViewById(R.id.gridImage);
        int image_index = cursor.getColumnIndex("path");

        File image = new File(cursor.getString(image_index));

        Picasso.with(context)
                .load(image)
                .noFade()
                .placeholder(R.drawable.higlight_color)
                .error(R.drawable.ic_launcher_background)
                .resize(320, 320)
                .centerCrop()
                .into(gridImage);


    }
}

