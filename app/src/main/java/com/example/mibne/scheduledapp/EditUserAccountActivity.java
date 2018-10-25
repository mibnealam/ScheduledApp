package com.example.mibne.scheduledapp;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

public class EditUserAccountActivity extends AppCompatActivity {

    private String TAG = "EditUserAccountActivity";

    private String uid;
    private String userName;
    private String userId;
    private String userPhone;
    private String userEmail;
    private String userPortraitUrl;
    private String userOrganization;
    private String userDepartment;

    private ImageView imageView;

    private TextInputLayout userNameTextInputLayout;
    private TextInputLayout userIdTextInputLayout;
    private TextInputLayout userEmailTextInputLayout;
    private TextInputLayout userPhoneTextInputLayout;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_account);

        getSupportActionBar().setTitle("Edit Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        uid = bundle.getString("uid");
        userName = bundle.getString("userName");
        userId = bundle.getString("userId");
        userPhone = bundle.getString("phone");
        userEmail = bundle.getString("email");
        userPortraitUrl = bundle.getString("photoUrl");
        userOrganization = bundle.getString("organization");
        userDepartment = bundle.getString("department");

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_edit_user_account);
        progressBar.setVisibility(View.GONE);

        imageView = (ImageView) findViewById(R.id.edit_portrait_image_view);
        userNameTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_name_wrapper);
        userIdTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_id_wrapper);
        userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_email_wrapper);
        userPhoneTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_phone_wrapper);

        Glide.with(imageView).load(userPortraitUrl).into(imageView);
        userNameTextInputLayout.getEditText().setText(userName);
        userIdTextInputLayout.getEditText().setText(userId);
        userEmailTextInputLayout.getEditText().setText(userEmail);
        userPhoneTextInputLayout.getEditText().setText(userPhone);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
