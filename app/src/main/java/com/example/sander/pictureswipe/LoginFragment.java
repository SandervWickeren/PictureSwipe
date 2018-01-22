package com.example.sander.pictureswipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass. Used to login
 * at FireBase, so favorites can be saved online.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button cBin = v.findViewById(R.id.clearBin);
        cBin.setOnClickListener(this);
        Button cPictures = v.findViewById(R.id.clearPictures);
        cPictures.setOnClickListener(this);
        Button dDelete = v.findViewById(R.id.deepCleanPictures);
        dDelete.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        SqliteDatabase db = SqliteDatabaseSingleton.getInstance(getActivity().getApplicationContext());
        switch (view.getId()) {
            case R.id.clearBin:
                db.deleteAllFromList();
                Toast.makeText(getActivity(), "Succesfully cleared bin", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clearPictures:
                db.deleteAllPictures();
                Toast.makeText(getActivity(), "Succesfully cleared pictures", Toast.LENGTH_SHORT).show();
                break;
            case R.id.deepCleanPictures:
                db.deepDelete();
                Toast.makeText(getActivity(), "Succesfully deep deleted", Toast.LENGTH_SHORT).show();
        }

    }
}
