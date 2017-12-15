/*
 * LoginFragment
 * Tessa Wagenaar
 * This fragment lets the user log in to the firebase database.
 * If the user is already logged in a logout button will
 * appear instead of the login fields.
 * To register a new user the same fields as the login fields
 * can be used, but the user must hit register instead of login.
 */

package a11021047.finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    TextView emailField;
    TextView passwordField;
    TextView emailText;
    TextView passwordText;
    TextView errorText;
    Button loginButton;
    Button registerButton;
    Button signoutButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    /*
     * onViewStateRestored
     * Call the original method for this function and update
     * the User Interface to the user state.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        updateUI(mAuth.getCurrentUser());
    }

    /*
     * onCreateView
     * Create the layout, locate the needed fields in the layout
     * and set listeners to the buttons.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailField = view.findViewById(R.id.emailField);
        passwordField = view.findViewById(R.id.passwordField);
        emailText = view.findViewById(R.id.emailText);
        passwordText = view.findViewById(R.id.passwordText);
        errorText = view.findViewById(R.id.login_error);

        // Set onClickListeners on the buttons
        loginButton = view.findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        registerButton = view.findViewById(R.id.register);
        registerButton.setOnClickListener(this);
        signoutButton = view.findViewById(R.id.signout);
        signoutButton.setOnClickListener(this);

        return view;
    }

    /*
     * onClick
     * Perform the appropiate action when one of the buttons
     * is pressed
     * Log in:
     *  Get the data and log in to firebase
     * Register:
     *  Get the data and register a new user to firebase
     * Sign Out:
     *  Sign out the current user
     */
    @Override
    public void onClick(View view) {
        String email, password;
        switch (view.getId()) {
            case R.id.login:
                email = emailField.getText().toString();
                password = passwordField.getText().toString();
                signIn(email, password);
                break;
            case R.id.register:
                email = emailField.getText().toString();
                password = passwordField.getText().toString();
                createAccount(email, password);
                break;
            case R.id.signout:
                signOut();
                break;
        }
    }

    /*
     * updateUI
     * Update the User Interface to match the user state
     * User is null:
     *  Show Login/Register fields
     * User is set:
     *  Show logout buttons
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            emailField.setVisibility(View.GONE);
            emailText.setVisibility(View.GONE);
            passwordField.setVisibility(View.GONE);
            passwordText.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            signoutButton.setVisibility(View.VISIBLE);
        } else {
            emailField.setVisibility(View.VISIBLE);
            emailText.setVisibility(View.VISIBLE);
            passwordField.setVisibility(View.VISIBLE);
            passwordText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            signoutButton.setVisibility(View.GONE);
        }
    }

    /*
     * createAccount
     * Register a new user to the firebase database.
     */
    public void createAccount(String email, String password) {
        if (validateForm()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("signed in", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                errorText.setVisibility(View.GONE);
                                updateUI(user);
                                LoginFragment.this.dismiss();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("failed", "createUserWithEmail:failure", task.getException());
                                if (task.getException() != null) {
                                    errorText.setText(task.getException().getMessage());
                                    errorText.setVisibility(View.VISIBLE);
                                }
                                updateUI(null);
                            }
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "Please enter email and password.",
                    Toast.LENGTH_SHORT).show();

        }
    }

    /*
     * signIn
     * Sign the user in to the firebase database
     */
    public void signIn(String email, String password) {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("signed in", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                errorText.setVisibility(View.GONE);
                                updateUI(user);
                                LoginFragment.this.dismiss();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("failed", "signInWithEmail:failure", task.getException());
                                if (task.getException() != null) {
                                    errorText.setText(task.getException().getMessage());
                                    errorText.setVisibility(View.VISIBLE);
                                }
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    /*
     * validateForm
     * return true if the email field and password field
     * contain data. If a field does not contain the data needed,
     * set an error on the field and return false
     */
    private boolean validateForm() {
        Log.d("validate", "entered");
        boolean valid = true;

        // Check the email field.
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        // Check the password field
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }
        return valid;
    }

    /*
     * Sign the user out and update the user interface to show
     * the login/register fields
     */
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
        this.dismiss();
    }

}
