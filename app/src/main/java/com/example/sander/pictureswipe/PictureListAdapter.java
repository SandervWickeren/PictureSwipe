package com.example.sander.pictureswipe;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * ... TODO
 */

public class PictureListAdapter extends ResourceCursorAdapter {


    public PictureListAdapter(Context context, Cursor c) {
        super(context, R.layout.image_row_layout, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        System.out.println("Bindviewing");
        TextView rowText = view.findViewById(R.id.name);
        int text_index = cursor.getColumnIndex("path");
        rowText.setText(cursor.getString(text_index));

    }
}
