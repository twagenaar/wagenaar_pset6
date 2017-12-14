package a11021047.finalproject;


import android.app.Activity;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.concurrent.Executor;


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

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        updateUI(mAuth.getCurrentUser());
    }

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
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("failed", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
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
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("failed", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
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

    // https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java
    private boolean validateForm() {
        Log.d("validate", "entered");
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Log.d("validate", "email empty");
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Log.d("validate", "password empty");
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }
        Log.d("validate", "end");
        return valid;
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

}
