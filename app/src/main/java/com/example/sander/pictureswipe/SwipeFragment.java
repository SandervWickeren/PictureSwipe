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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
    TextView positionss, progressss;
    Button bin, fav, next;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swipe, container, false);

        mSwipeStack = view.findViewById(R.id.swipeStack);
        positionss = view.findViewById(R.id.positions);
        progressss = view.findViewById(R.id.progressions);
        bin = view.findViewById(R.id.addBin);
        fav = view.findViewById(R.id.addFavorite);
        next = view.findViewById(R.id.next);
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
            imagesPointer += 1;
            System.out.println("Left");
        }

        @Override
        public void onViewSwipedToRight(int position) {
            String swipedElement = swipeStackAdapter.getItem(position);
            imagesPointer += 1;
            System.out.println("Right");
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
            textViewCard.setText(mData.get(position));
            positionss.setText(String.valueOf(imagesPointer));

            ImageView imageView = convertView.findViewById(R.id.cardImage);

            File file = new File(mData.get(position));
            try {
                Picasso.with(getContext()).load(file).error(R.drawable.higlight_color).resize(800, 800).centerInside().into(imageView);
            } catch (Exception e) {
                System.out.println("Couldn't load: " + mData.get(position));
            }
            return convertView;
        }


    }

    public class SwipeProgressListener implements SwipeStack.SwipeProgressListener {

        @Override
        public void onSwipeStart(int position) {

        }

        @Override
        public void onSwipeProgress(int position, float progress) {
            //positionss.setText(String.valueOf(position));
            progressss.setText(String.valueOf(progress));
        }

        @Override
        public void onSwipeEnd(int position) {

        }
    }








}
