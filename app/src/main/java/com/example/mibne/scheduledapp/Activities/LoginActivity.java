package com.example.mibne.scheduledapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.mibne.scheduledapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;

    private TextInputLayout userEmailTextInputLayout;
    private TextInputLayout userPasswordTextInputLayout;
    private String email;
    private String password;

    private ScrollView scrollView;
    private View loadingIndicator;

    private final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        loadingIndicator = findViewById(R.id.sign_in_loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.user_login_email_wrapper);
        userPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.user_login_password_wrapper);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.action_log_in_button).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.forgot_password_button).setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [START validateUserEmail]
    public static boolean validateEmail( TextInputLayout textInputLayout) {
        String userEmailInput = textInputLayout.getEditText().getText().toString().trim();

        if (userEmailInput.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." +
                "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
                "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@" +
                "(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*" +
                "[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]" +
                "|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
                "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            textInputLayout.setError(null);
            return true;
        } else {
            textInputLayout.setError(" ");
            return false;
        }
    }
    // [END validateUserEmail]

    // [START validateUserPassword]
    public static boolean validateUserPassword(TextInputLayout textInputLayout) {
        String userPasswordInput = textInputLayout.getEditText().getText().toString().trim();
        if (userPasswordInput.length() < 6) {
            textInputLayout.setError("short");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
    // [END validateUserPassword]

    // [START confirmInput]
    public boolean confirmInput() {
        if (!validateEmail(userEmailTextInputLayout) | !validateUserPassword(userPasswordTextInputLayout)) {
            return false;
        } else {
            email = userEmailTextInputLayout.getEditText().getText().toString().trim();
            password = userPasswordTextInputLayout.getEditText().getText().toString();
            return true;
        }
    }
    // [END confirmInput]

    // [START logIn]
    private void logIn() {
        if (checkConnection(getApplicationContext())) {
            if (confirmInput()) {
                scrollView = findViewById(R.id.login_form);
                scrollView.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sharedPreferences.edit().putString("loginEmail", email).apply();
                                    sharedPreferences.edit().putString("loginPassword", password).apply();
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Wrong Email/Password",
                                            Toast.LENGTH_LONG).show();
                                    updateUI(null);
                                    scrollView.setVisibility(View.VISIBLE);
                                    loadingIndicator.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.prompt_no_internet_connection, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    // Method to manually check connection status
    public static boolean checkConnection(Context context) {
        boolean isConnected;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            return true;
        } else {
            return false;
        }

    }
    // [END logIn]

    // [START updateUI]
    private void updateUI(@Nullable FirebaseUser account) {
        if (account != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.v("update UI :", "account is null");
        }
    }
    // [END updateUI]

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_log_in_button:
                logIn();
                break;
            case R.id.forgot_password_button:
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                startActivity(intent);
                default:
                    break;
        }
    }
}
