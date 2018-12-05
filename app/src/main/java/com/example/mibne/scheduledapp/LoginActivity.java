package com.example.mibne.scheduledapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

//    private TextInputLayout userEmailTextInputLayout;
//    private TextInputLayout userPasswordTextInputLayout;
//    private String email;
//    private String password;

    private LinearLayout linearLayout;
    private View loadingIndicator;


    private final  int RC_SIGN_IN = 9001;
    private final String TAG = "LoginActivity";

    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingIndicator = findViewById(R.id.sign_in_loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

//        userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.user_login_email_wrapper) ;
//        userPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.user_login_password_wrapper) ;

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.action_log_in_with_google_button).setOnClickListener((View.OnClickListener) this);
//        findViewById(R.id.forgot_password_button).setOnClickListener((View.OnClickListener) this);
//        findViewById(R.id.action_log_in_button).setOnClickListener((View.OnClickListener) this);
//        findViewById(R.id.action_create_account).setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [START validateUserEmail]
//    private boolean validateUserEmail() {
//        String userEmailInput = userEmailTextInputLayout.getEditText().getText().toString().trim();
//
//        if (userEmailInput.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
//            userEmailTextInputLayout.setError(null);
//            return true;
//        } else {
//            userEmailTextInputLayout.setError("Invalid email!");
//            return false;
//        }
//    }
    // [END validateUserEmail]

    // [START validateUserPassword]
//    private boolean validateUserPassword() {
//        String userPasswordInput = userPasswordTextInputLayout.getEditText().getText().toString().trim();
//
//        if (userPasswordInput.isEmpty()) {
//            userPasswordTextInputLayout.setError("Enter password please");
//            return false;
//        } else {
//            userPasswordTextInputLayout.setError(null);
//            return true;
//        }
//    }
    // [END validateUserPassword]

    // [START confirmInput]
//    public boolean confirmInput() {
//        if (!validateUserEmail() | !validateUserPassword()) {
//            return false;
//        } else {
//            email = userEmailTextInputLayout.getEditText().getText().toString().trim();
//            password = userPasswordTextInputLayout.getEditText().getText().toString();
//            return true;
//        }
//    }
    // [END confirmInput]

    // [START logIn]
//    private void logIn() {
//        if (confirmInput()) {
//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success, update UI with the signed-in user's information
//                                Log.d(TAG, "signInWithEmail:success");
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                updateUI(user);
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                Toast.makeText(LoginActivity.this, "Wrong Email/Password",
//                                        Toast.LENGTH_LONG).show();
//                                updateUI(null);
//                            }
//                        }
//                    });
//        }
//    }
    // [END logIn]

    // [START forgotPassword]
//    private void forgotPassword() {
//        Intent intent = new Intent(this, ResetPasswordActivity.class);
//        startActivity(intent);
//    }
    // [END forgotPassword]

    // [START createAccount]
//    private void createAccount() {
//        Intent intent = new Intent(this, CreateAccountActivity.class);
//        startActivity(intent);
//    }
    // [END createAccount]

    // [START signIn]
    private void signIn() {
        Log.v("test", "User Starts sign in!");
        linearLayout = findViewById(R.id.login_form);
        linearLayout.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START onActivityResult]

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                linearLayout.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                linearLayout.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        }
    }
    // [START firebaseAuthWithGoogle]
    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.snackbar), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    // [END firebaseAuthWithGoogle]

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
//            case R.id.action_log_in_button:
//                logIn();
//                break;
//            case R.id.action_create_account:
//                createAccount();
//                break;
            case R.id.action_log_in_with_google_button:
                signIn();
                break;
//            case R.id.forgot_password_button:
//                forgotPassword();
//                break;
                default:
                    break;
        }
    }
}
