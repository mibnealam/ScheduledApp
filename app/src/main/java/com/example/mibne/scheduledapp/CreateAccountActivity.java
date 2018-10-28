package com.example.mibne.scheduledapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {

    private final String TAG = "CreateAccountActivity";

    private TextInputLayout userEmailTextInputLayout;
    private TextInputLayout userPasswordTextInputLayout;
    private TextInputLayout confirmUserPasswordTextInputLayout;
    private boolean passwordMatched = false;
    private String email;
    private String password;

    private Button button;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.user_email_wrapper) ;
        userPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.user_password_wrapper) ;
        confirmUserPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.confirm_password_wrapper);

        button = (Button) findViewById(R.id.action_create_account_button);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        confirmUserPasswordTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strPass1 = userPasswordTextInputLayout.getEditText().getText().toString();
                String strPass2 = confirmUserPasswordTextInputLayout.getEditText().getText().toString();
                if (strPass1.equals(strPass2)) {
                    confirmUserPasswordTextInputLayout.setError(null);
                    passwordMatched = true;
                } else {
                    confirmUserPasswordTextInputLayout.setError(" ");
                    passwordMatched = false;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmInput()) {
                    createAccount();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // [START validateUserEmail]
    private boolean validateUserEmail() {
        String userEmailInput = userEmailTextInputLayout.getEditText().getText().toString().trim();

        if (userEmailInput.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            userEmailTextInputLayout.setError(null);
            email = userEmailInput;
            return true;
        } else {
            userEmailTextInputLayout.setError("Invalid email!");
            return false;
        }
    }
    // [END validateUserEmail]
    // [START validateUserPassword]
    private boolean validateUserPassword() {
        String userPasswordInput = userPasswordTextInputLayout.getEditText().getText().toString().trim();
        if (userPasswordInput.isEmpty()) {
            userPasswordTextInputLayout.setError(" ");
            return false;
        } else {
            userPasswordTextInputLayout.setError(null);
            password = userPasswordInput;
            return true;
        }
    }
    // [END validateUserPassword]

    // [START confirmInput]
    public boolean confirmInput() {
        if (!validateUserEmail() | !validateUserPassword() | !passwordMatched) {
            return false;
        } else {
            email = userEmailTextInputLayout.getEditText().getText().toString().trim();
            password = userPasswordTextInputLayout.getEditText().getText().toString();
            return true;
        }
    }
    // [END confirmInput]

    // [START updateUI]
    private void updateUI(@Nullable FirebaseUser account) {
        if (account != null) {
            Intent intent = new Intent(this, UserInfoActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.v("update UI :", "account is null");
        }
    }
    // [END updateUI]

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
