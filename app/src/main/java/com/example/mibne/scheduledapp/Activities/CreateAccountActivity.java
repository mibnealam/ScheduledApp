package com.example.mibne.scheduledapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mibne.scheduledapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.mibne.scheduledapp.Activities.LoginActivity.validateEmail;

public class CreateAccountActivity extends AppCompatActivity {

    private final String TAG = "CreateAccountActivity";

    private TextInputLayout userEmailTextInputLayout;
    private TextInputLayout userPasswordTextInputLayout;
    private TextInputLayout confirmUserPasswordTextInputLayout;

    private String email = "";
    private String password = "";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        userEmailTextInputLayout = findViewById(R.id.user_email_wrapper);
        userPasswordTextInputLayout = findViewById(R.id.user_password_wrapper);
        confirmUserPasswordTextInputLayout = findViewById(R.id.confirm_password_wrapper);

        findViewById(R.id.action_create_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmInput()) {
                    signUp();
                }
            }
        });
    }

    private void signUp() {
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

    public boolean confirmInput() {
        if (!validateEmail(userEmailTextInputLayout) | !confirmPasswords(userPasswordTextInputLayout, confirmUserPasswordTextInputLayout)) {
            return false;
        } else {
            email = userEmailTextInputLayout.getEditText().getText().toString().trim();
            password = userPasswordTextInputLayout.getEditText().getText().toString();
            return true;
        }
    }

    // [START validateUserPassword]
    public static boolean confirmPasswords(TextInputLayout password, TextInputLayout passwordConfirmation) {
        String userPasswordInput1 = password.getEditText().getText().toString().trim();
        String userPasswordInput2 = passwordConfirmation.getEditText().getText().toString().trim();
        if (userPasswordInput1.length() < 6) {
            password.setError("short");
            return false;
        } else {
            password.setError(null);
            if (userPasswordInput1.equals(userPasswordInput2)) {
                return true;
            } else {
                passwordConfirmation.setError("Not matched");
                return false;
            }
        }
    }
    // [END validateUserPassword]

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
}
