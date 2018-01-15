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
public class SwipeFragment extends Fragment {

    private List<String> images;
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter swipeStackAdapter;
    private SwipeStackListener swipeStackListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swipe, container, false);

        mSwipeStack = view.findViewById(R.id.swipeStack);
        ImageView imageView = view.findViewById(R.id.content);

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
        mSwipeStack.setAdapter(swipeStackAdapter);
        mSwipeStack.setListener(swipeStackListener);




        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public class SwipeStackListener implements SwipeStack.SwipeStackListener{

        @Override
        public void onViewSwipedToLeft(int position) {
            String swipedElement = swipeStackAdapter.getItem(position);
            Toast.makeText(getActivity(), "left",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onViewSwipedToRight(int position) {
            String swipedElement = swipeStackAdapter.getItem(position);
            Toast.makeText(getActivity(), "right",
                    Toast.LENGTH_SHORT).show();
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

            return convertView;
        }
    }








}
