package com.example.sander.pictureswipe;


import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import link.fls.swipestack.SwipeStack;




/**
 * A simple {@link Fragment} subclass.
 */
public class SwipeFragment extends Fragment implements View.OnClickListener {

    private List<String> images;
    private Integer imagesPointer;
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter swipeStackAdapter;
    private SwipeStackListener swipeStackListener;
    private SwipeProgressListener swipeProgressListener;
    Button bin, fav, next;
    RelativeLayout overlay;
    ImageView overlayIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swipe, container, false);

        mSwipeStack = view.findViewById(R.id.swipeStack);
        bin = view.findViewById(R.id.addBin);
        fav = view.findViewById(R.id.addFavorite);
        next = view.findViewById(R.id.next);
        overlay = view.findViewById(R.id.swipeColor);
        overlayIcon = view.findViewById(R.id.iconOverlay);
        bin.setOnClickListener(this);
        fav.setOnClickListener(this);
        next.setOnClickListener(this);

        try {
            Bundle bundle = this.getArguments();
            Uri pictureUri = bundle.getParcelable("uri");

            //Picasso.with(getContext()).load(pictureUri).into(imageView);

            LoadImages loadImages = new LoadImages();
            images = loadImages.getList(getContext(), pictureUri);
            System.out.println(images.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
        swipeStackAdapter = new SwipeStackAdapter(images);
        swipeStackListener = new SwipeStackListener();
        swipeProgressListener = new SwipeProgressListener();
        imagesPointer = 0;
        mSwipeStack.setAdapter(swipeStackAdapter);
        mSwipeStack.setListener(swipeStackListener);
        mSwipeStack.setSwipeProgressListener(swipeProgressListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addBin:
                mSwipeStack.swipeTopViewToLeft();
                break;
            case R.id.addFavorite:
                String swipedElement = swipeStackAdapter.getItem(mSwipeStack.getCurrentPosition());
                addToList(swipedElement, "favorites");
                mSwipeStack.swipeTopViewToRight();
                break;
            case R.id.next:
                mSwipeStack.swipeTopViewToRight();
                break;
        }
    }


    public class SwipeStackListener implements SwipeStack.SwipeStackListener{

        @Override
        public void onViewSwipedToLeft(int position) {
            String swipedElement = swipeStackAdapter.getItem(position);
            Log.d("INFO", "onViewSwipedToLeft:" + swipedElement);
            System.out.println("Left");

            // Add image to SQlite bin table.
            addToList(swipedElement, "bin");

            // Reset overlay color
            overlay.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            overlayIcon.setAlpha(0f);

        }

        @Override
        public void onViewSwipedToRight(int position) {
            String swipedElement = swipeStackAdapter.getItem(position);
            Log.d("INFO", "onViewSwipedToRight:" + swipedElement);
            System.out.println("Right");

            // Add image to database.
            addToPictures(swipedElement);

            // Reset overlay color
            overlay.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            overlayIcon.setAlpha(0f);
        }

        @Override
        public void onStackEmpty() {
            Toast.makeText(getActivity(), "Empty stack", Toast.LENGTH_SHORT).show();
        }

    }


    public class SwipeStackAdapter extends BaseAdapter {

        private List<String> mData;

        public SwipeStackAdapter(List<String> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.card_layout, parent, false);
            }

            TextView textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);

            String[] splitPath = mData.get(position).split("/");
            String imageName = splitPath[splitPath.length - 1];
            textViewCard.setText(imageName);


            ImageView imageView = convertView.findViewById(R.id.cardImage);

            File file = new File(mData.get(position));
            try {
                Picasso.with(getContext()).load(file).error(R.drawable.higlight_color).fit().centerInside().into(imageView);
            } catch (Exception e) {
                System.out.println("Couldn't load: " + mData.get(position));
            }
            return convertView;
        }
    }

    public class SwipeProgressListener implements SwipeStack.SwipeProgressListener {

        @Override
        public void onSwipeStart(int position) {}

        @Override
        public void onSwipeProgress(int position, float progress) {
            setIconOverlay(progress);
        }

        @Override
        public void onSwipeEnd(int position) {}
    }

    /**
     * Used to calculate the correct alpha and color for the overlay when moving
     * the picture to either left or right.
     * @param progress A float that represents how far it's from the end of the screen
     */
    public void setIconOverlay(float progress) {
        String baseColorNext = "#29ABA4";
        String baseColorBin = "#EB7260";
        double prog;
        float alpha;

        // Only start changing overlay color when the progress is more then
        // 0.05 to make it look smoother.
        if (progress > 0.05) {
            overlayIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check_black_24dp));
            overlayIcon.setColorFilter(Color.parseColor(baseColorNext));
            alpha = (float)(Math.pow(150, (progress - 0.05)) / 50);

        } else if (progress < -0.05) {
            overlayIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_black_24dp));
            overlayIcon.setColorFilter(Color.parseColor(baseColorBin));
            alpha = (float)(Math.pow(150, (-1 * progress + 0.05)) / 50);

        } else {
            alpha = 0;
        }

        overlayIcon.setAlpha(alpha);

    }

    public void addToPictures(String path) {
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        String[] slicedPath = path.split("/");
        String name = slicedPath[slicedPath.length - 1];
        String album = slicedPath[slicedPath.length - 2];

        System.out.println(name + "::" + album);

        System.out.println(db.inPictures(name));
        if (!(db.inPictures(name))) {
            db.insertPicture(name, album, path);
        }
    }

    public void addToList(String path, String table) {
        // Put in 'done' db.
        addToPictures(path);

        // Add to bin db
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        String[] slicedPath = path.split("/");
        String name = slicedPath[slicedPath.length - 1];

        System.out.println(db.getIdFromName(name));

        db.insertToList(table, db.getIdFromName(name));
    }

}
