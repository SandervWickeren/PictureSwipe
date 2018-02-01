package com.example.sander.pictureswipe;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import com.squareup.picasso.Picasso;
import java.io.File;

/**
 * Class that is used to place the images correct in the grid. It extends the ResourceCursorAdapter
 * to be able to directly get the information from the SQlite database.
 */
public class PictureGridAdapter extends ResourceCursorAdapter {


    public PictureGridAdapter(Context context, Cursor c) {
        super(context, R.layout.image_grid_layout, c);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Bind view.
        ImageView gridImage = view.findViewById(R.id.gridImage);

        // Get the correct path representing the image.
        int image_index = cursor.getColumnIndex("path");

        // Convert to file.
        File image = new File(cursor.getString(image_index));

        // Load into the ImageView using Picasso.
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