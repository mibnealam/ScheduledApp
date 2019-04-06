package com.example.mibne.scheduledapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditUserAccountActivity extends AppCompatActivity {

    private String TAG = "EditUserAccountActivity";

    private static final int REQUEST_IMAGE = 2;

    private User user;

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
    private ProgressBar progressBarPhotoUpload;

    private Button updateButton;
    private Button passwordResetButton;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReferance;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_account);

        getSupportActionBar().setTitle("Edit Account");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
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

        user = new User();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("profile_photos");
        mUserDatabaseReferance = mFirebaseDatabase.getReference().child("users/" + uid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_edit_user_account);
        progressBar.setVisibility(View.GONE);
        progressBarPhotoUpload = (ProgressBar) findViewById(R.id.progress_bar_user_photo);
        progressBarPhotoUpload.setVisibility(View.GONE);

        imageView = (ImageView) findViewById(R.id.edit_portrait_image_view);
        userNameTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_name_wrapper);
        userIdTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_id_wrapper);
        userEmailTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_email_wrapper);
        userPhoneTextInputLayout = (TextInputLayout) findViewById(R.id.edit_user_phone_wrapper);

        updateButton = (Button) findViewById(R.id.update_info_button);
        passwordResetButton = (Button) findViewById(R.id.reset_password_button);

        Glide.with(imageView).load(userPortraitUrl).into(imageView);
        userNameTextInputLayout.getEditText().setText(userName);
        userNameTextInputLayout.setEnabled(false);
        userIdTextInputLayout.getEditText().setText(userId);
        userIdTextInputLayout.setEnabled(false);
        userEmailTextInputLayout.getEditText().setText(userEmail);
        userEmailTextInputLayout.setEnabled(false);
        userPhoneTextInputLayout.getEditText().setText(userPhone);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });
        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    Toast.makeText(EditUserAccountActivity.this, "A password reset email is sent.", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            final Uri selectedImageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] dataImage = baos.toByteArray();
            imageView.setImageBitmap(bitmap);
            progressBarPhotoUpload.setVisibility(View.VISIBLE);
            // Get a reference to store file at chat_photos/<FILENAME>
            final StorageReference photoRef = mStorageReference.child(uid).child("photo" + uid);
            // Upload file to Firebase Storage
            photoRef.putBytes(dataImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.v("lalall", downloadUri.toString());
                        mUserDatabaseReferance.child("photoUrl").setValue(downloadUri.toString());
                        progressBarPhotoUpload.setVisibility(View.GONE);
                        Toast.makeText(EditUserAccountActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failures
                        Toast.makeText(EditUserAccountActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateUserName() {
        String userNameInput = userNameTextInputLayout.getEditText().getText().toString().trim();

        if (userNameInput.isEmpty()) {
            userNameTextInputLayout.setError("Don\'t have one?");
            return false;
        } else if (userNameInput.length() > 60) {
            userNameTextInputLayout.setError("That long?");
            return false;
        } else if (userNameInput.length() < 2) {
            userNameTextInputLayout.setError("Too short");
            return false;
        } else {
            userNameTextInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateUserId() {
        String userIdInput = userIdTextInputLayout.getEditText().getText().toString().trim();

        if (userIdInput.isEmpty()) {
            userIdTextInputLayout.setError("Don\'t have one?");
            return false;
        } else if (userIdInput.length() > 20) {
            userIdTextInputLayout.setError("That long?");
            return false;
        } else if (userIdInput.length() < 2) {
            userIdTextInputLayout.setError("Too short");
            return false;
        } else {
            userIdTextInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateUserEmail() {
        String userEmailInput = userEmailTextInputLayout.getEditText().getText().toString().trim();

        if (userEmailInput.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            userEmailTextInputLayout.setError(null);
            return true;
        } else {
            userEmailTextInputLayout.setError("Invalid email!");
            return false;
        }
    }

    private boolean validateUserPhone() {
        String userPhoneInput = userPhoneTextInputLayout.getEditText().getText().toString().trim();

        if (userPhoneInput.matches("[+]|[8]{2}|[0][1][3|5-9][0-9]{8}")) {
            userPhoneTextInputLayout.setError(null);
            return true;
        } else {
            userPhoneTextInputLayout.setError("Invalid number!");
            return false;
        }
    }

    public boolean confirmInput() {
        if (!validateUserName() | !validateUserId() | !validateUserEmail() | !validateUserPhone()) {
            return false;
        } else {
            user.setName(userNameTextInputLayout.getEditText().getText().toString().trim());
            user.setUsername(userIdTextInputLayout.getEditText().getText().toString().trim());
            user.setEmail(userEmailTextInputLayout.getEditText().getText().toString().trim());
            user.setPhone(userPhoneTextInputLayout.getEditText().getText().toString().trim());

            return true;
        }
    }

    private void updateInfo() {
        if (confirmInput()) {

            final Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("name", user.getName());
            childUpdates.put("username", user.getUsername());
            childUpdates.put("email", user.getEmail());
            childUpdates.put("phone", user.getPhone());

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            firebaseUser.updateEmail(user.getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                                mUserDatabaseReferance.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditUserAccountActivity.this, "Account Info Updated Successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditUserAccountActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
