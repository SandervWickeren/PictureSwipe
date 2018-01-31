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
    TextView errorText;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button register = v.findViewById(R.id.register);
        register.setOnClickListener(this);
        Button login = v.findViewById(R.id.login);
        login.setOnClickListener(this);

        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        errorText = v.findViewById(R.id.errorText);

        // Get firebase reference
        mAuth = FirebaseAuth.getInstance();

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            errorText.setText("Email or password is incorrect.");
            errorText.setVisibility(View.VISIBLE);
        } else {
            errorText.setVisibility(View.INVISIBLE);
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
                                errorText.setText("Can't make a connection to the server.");
                                errorText.setVisibility(View.VISIBLE);

                                // Other error
                            } catch (Exception e) {
                                errorText.setText("Email or password is incorrect.");
                                errorText.setVisibility(View.VISIBLE);
                            }
                            // Log Error
                            Log.w("email", "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }
}
