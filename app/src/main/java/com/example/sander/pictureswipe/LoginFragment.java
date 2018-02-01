package com.example.sander.pictureswipe;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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


/**
 *  Used to handle the login at FireBase, so favorites can be saved online.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    EditText email, password;
    TextView errorText;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Bind views and set listeners.
        Button register = view.findViewById(R.id.register);
        register.setOnClickListener(this);
        Button login = view.findViewById(R.id.login);
        login.setOnClickListener(this);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        errorText = view.findViewById(R.id.errorText);

        // Get firebase reference
        mAuth = FirebaseAuth.getInstance();

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                // Launch register fragment.
                Fragment fragment = new RegisterFragment();
                ((LoginActivity)getActivity()).replaceFragment(fragment);
                break;
            case R.id.login:
                loginCheck(email.getText().toString(), password.getText().toString());
                break;
        }

    }

    /**
     * Function that checks if the input is long enough to be valid. If not it makes an error
     * text visible for the user. If it's valid it launches logIn.
     * @param email from the login.
     * @param pass password from the login.
     */
    public void loginCheck(String email, String pass) {
        if (email.length() == 0 || pass.length() == 0) {
            errorText.setText("Email or password is incorrect.");
            errorText.setVisibility(View.VISIBLE);
        } else {
            errorText.setVisibility(View.INVISIBLE);
            logIn(getView(), email, pass);
        }
    }


    /**
     * Handles Firebase authorisation.
     * @param view current view.
     * @param email from the account.
     * @param password from the account.
     */
    public void logIn(View view, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(getActivity(), "Succesfully logged in",
                            Toast.LENGTH_SHORT).show();

                    // Return to previous screen
                    getActivity().finish();
                } else {
                    // Check failure and give feedback
                    try {
                        throw task.getException();

                    } catch (FirebaseNetworkException e) {
                        // Network error, update text.
                        errorText.setText("Can't make a connection to the server.");
                        errorText.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        // Other error, update text.
                        errorText.setText("Email or password is incorrect.");
                        errorText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}