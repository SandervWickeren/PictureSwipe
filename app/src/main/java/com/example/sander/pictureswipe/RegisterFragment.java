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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;


/**
 * Used to register at FireBase so the user can save his or her favorites.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    EditText email, password, passwordRepeat;
    Button back, register;
    TextView errorText;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Bind view and set listeners.
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        passwordRepeat = view.findViewById(R.id.passwordRepeat);
        back = view.findViewById(R.id.back);
        register = view.findViewById(R.id.register);
        errorText = view.findViewById(R.id.errorText);
        back.setOnClickListener(this);
        register.setOnClickListener(this);

        // Get FireBase reference
        mAuth = FirebaseAuth.getInstance();

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                // Get back to login Fragment.
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.register:
                registerCheck(email.getText().toString(), password.getText().toString(),
                        passwordRepeat.getText().toString());
                break;
        }
    }


    /**
     * Check if the user entered valid credentials in the editTexts.
     * @param email from editText.
     * @param pass from editText.
     * @param passRepeat from editText.
     */
    public void registerCheck(String email, String pass, String passRepeat) {
        Integer noError = 1;

        // Check length
        if (pass.length() < 6) {
            noError = 0;
            errorText.setText("Your password is too short.");
        }

        // Check if passwords match.
        if (!(Objects.equals(pass, passRepeat))) {
            noError = 0;
            errorText.setText("The passwords don't match.");
        }

        // Check email length.
        if (email.length() < 5) {
            noError = 0;
            errorText.setText("Your email is not correct.");
        }

        // Only create user when there is no error.
        if (noError == 1) {
            errorText.setVisibility(View.INVISIBLE);
            createUser(email, pass);
        } else {
            // Notify user about error.
            errorText.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Used to register the user at Firebase.
     * @param email the wanted email
     * @param password the password.
     */
    public void createUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Notify user
                            Toast.makeText(getActivity(),
                                    "Account succesfully created!", Toast.LENGTH_SHORT).show();

                            // Navigate back to start
                            getFragmentManager().popBackStackImmediate();
                        } else {
                            // Check failure
                            try {
                                throw task.getException();

                                // Email already in use.
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(getActivity(),
                                        "The entered email is already used. Please use another email",
                                        Toast.LENGTH_SHORT).show();

                                // Network problems
                            } catch (FirebaseNetworkException e) {
                                Toast.makeText(getActivity(),
                                        "Can't make a connection to the server.",
                                        Toast.LENGTH_SHORT).show();

                                // Other error
                            } catch (Exception e) {
                                Toast.makeText(getActivity(),
                                        "An error occured, please try it again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}