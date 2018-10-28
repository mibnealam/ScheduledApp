package com.example.mibne.scheduledapp;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private final String TAG = "ResetPasswordActivity";

    private TextInputLayout userEmailTextInputLayout;
    private String email;

    private Button button;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.user_login_email_wrapper) ;
        button = (Button) findViewById(R.id.action_reset_account_button);

        auth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserEmail()) {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPasswordActivity.this, "Reset Email Sent", Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    }
                                }
                            });
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
}
