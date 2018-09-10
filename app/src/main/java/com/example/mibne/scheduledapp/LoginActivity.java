package com.example.mibne.scheduledapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.sign_up_button).setOnClickListener((View.OnClickListener) this);
    }

    // [START signUp]
    private void signUp() {
        Intent signUpInIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpInIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_in_button:
                //logIn();
                break;
            case R.id.sign_up_button:
                signUp();
                break;
            case R.id.log_in_with_google_button:
                //signIn();
                break;
            //case R.id.disconnect_button:
            //revokeAccess();
            //break;
        }
    }
}
