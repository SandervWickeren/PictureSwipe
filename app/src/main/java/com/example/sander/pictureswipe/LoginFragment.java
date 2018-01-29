package com.example.sander.pictureswipe;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass. Used to login
 * at FireBase, so favorites can be saved online.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    EditText email, password;
    private FirebaseAuth mAuth;

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
        Button register = v.findViewById(R.id.register);
        register.setOnClickListener(this);
        Button login = v.findViewById(R.id.login);
        login.setOnClickListener(this);

        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);

        // Get firebase reference
        mAuth = FirebaseAuth.getInstance();

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
                break;
            case R.id.register:
                Fragment fragment = new RegisterFragment();
                ((LoginActivity)getActivity()).replaceFragment(fragment);
                break;
            case R.id.login:
                loginCheck(email.getText().toString(), password.getText().toString());
                break;
        }

    }

    public void loginCheck(String email, String pass) {
        if (email.length() == 0 || pass.length() == 0) {
            // Report to user
        } else {
            logIn(getView(), email, pass);
        }

    }

    public void logIn(View view, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign in", "signInWithEmail:success");
                            Toast.makeText(getActivity(),
                                    "Succesfully logged in",
                                    Toast.LENGTH_SHORT).show();

                            // Return to previous screen
                            getActivity().finish();


                        } else {
                            // Check failure and give feedback
                            try {
                                throw task.getException();

                                // Network problems
                            } catch (FirebaseNetworkException e) {
                                Toast.makeText(getActivity(),
                                        "Can't make a connection to the server.",
                                        Toast.LENGTH_SHORT).show();

                                // Other error
                            } catch (Exception e) {
                                Toast.makeText(getActivity(),
                                        "Please enter valid credentials.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // Log Error
                            Log.w("email", "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }
}
