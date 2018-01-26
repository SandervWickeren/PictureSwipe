package com.example.sander.pictureswipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {

    Button logOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        logOut = view.findViewById(R.id.logOut);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logOut:
                logOutProcessor();
                break;
        }
    }

    public void logOutProcessor() {
        FirebaseAuth.getInstance().signOut();

        // Pop from backstack to prevent going back illegally
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(this.getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Launch login Fragment
        LoginFragment fragment = new LoginFragment();
        ((MainActivity)getActivity()).replaceFragment(fragment);
    }
}
