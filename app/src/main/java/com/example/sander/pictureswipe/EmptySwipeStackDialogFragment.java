package com.example.sander.pictureswipe;


import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * This DialogFragment is used when the SwipeStack is empty / or the album is already
 * gone trough. The user gets the option to reset the images or to cancel and select another album.
 */
public class EmptySwipeStackDialogFragment extends DialogFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_swipe_stack_dialog, container, false);

        Button cancel = view.findViewById(R.id.cancel);
        Button yes = view.findViewById(R.id.yes);

        cancel.setOnClickListener(this);
        yes.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Fragment fragment = new SwipeSetupFragment();
        ((MainActivity)getActivity()).replaceFragment(fragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                getDialog().dismiss();
                break;
            case R.id.yes:
                clearAlbumFromPictures();
                getDialog().dismiss();
                break;
        }
    }

    public void clearAlbumFromPictures() {
        Bundle bundle = this.getArguments();
        String path = bundle.getString("path");

        String[] splitPath = path.split("/");
        String imageName = splitPath[splitPath.length - 1];

        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getContext());
        db.deleteAlbumFromPictures(imageName);

        String tag = SwipeFragment.class.getName();
        ((MainActivity)getActivity()).reloadFragment(tag);

    }
}
