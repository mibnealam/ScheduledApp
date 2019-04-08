package com.example.mibne.scheduledapp.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mibne.scheduledapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.mibne.scheduledapp.Activities.LoginActivity.checkConnection;
import static com.example.mibne.scheduledapp.Activities.LoginActivity.validateEmail;

public class ResetPasswordActivity extends AppCompatActivity {

    private final String TAG = "ResetPasswordActivity";

    private TextInputLayout userEmailTextInputLayout;

    private Button button;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.reset_user_email_wrapper);

        //userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.user_login_email_wrapper) ;
        button = (Button) findViewById(R.id.action_reset_account_button);

        auth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection(getApplicationContext())) {
                    if (validateEmail(userEmailTextInputLayout)) {
                        auth.sendPasswordResetEmail(userEmailTextInputLayout.getEditText().getText().toString().trim())
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
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.prompt_no_internet_connection, Snackbar.LENGTH_LONG);
                    snackbar.show();
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
}
