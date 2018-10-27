package com.example.mibne.scheduledapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_USERNAME_LENGTH_LIMIT = 30;
    public static final int DEFAULT_PHONE_NO_LENGTH_LIMIT = 11;

    private ProgressBar mProgressBar;
    private EditText mUsernameEditText;
    private EditText mUserPhoneNoEditText;
    private Button mNextButton;

    private String uid;
    private String mUserEmail;
    private String mUserOrganization;
    private String mUserDepartment;
    private String userId;
    private String mPhotoUrl;
    private String mUserRole;

    private String mUsername;

    private boolean usernameUpdaed = false;
    private boolean phoheUpdated = false;


    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mUsernameEditText = (EditText) findViewById(R.id.user_id);
        mUserPhoneNoEditText = (EditText) findViewById(R.id.user_phone_no);
        mNextButton = (Button) findViewById(R.id.next_button);

        /**
         * Get Users info
         */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            mUsername = user.getDisplayName();
            mUserEmail = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            mPhotoUrl = photoUrl.toString();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
        }

        mUserRole = "student";
        //Get organizations
        String[] organizationsArray = {"sub"};
        String[] departmentsArray = {"cse"};


        Spinner organizationSpinner = (Spinner) findViewById(R.id.organizations_spinner);
        Spinner departmentSpinner = (Spinner) findViewById(R.id.departments_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> organizationSpinnerAdapter;
        organizationSpinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, organizationsArray);

        ArrayAdapter<CharSequence> departmentSpinnerAdapter;
        departmentSpinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, departmentsArray);
        // Specify the layout to use when the list of choices appears
        organizationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        organizationSpinner.setAdapter(organizationSpinnerAdapter);
        departmentSpinner.setAdapter(departmentSpinnerAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        // Enable Next button when there's information
        mUsernameEditText.addTextChangedListener(userInfoTextWatcher);
        mUserPhoneNoEditText.addTextChangedListener(userInfoTextWatcher);
        mUsernameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_USERNAME_LENGTH_LIMIT)});
        mUserPhoneNoEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_PHONE_NO_LENGTH_LIMIT)});

        organizationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                mUserOrganization = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(null,"Please Select your organization", Toast.LENGTH_LONG).show();
            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                mUserDepartment = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Checks if user data exists and updates to the next UI
        //addUserChangeListener();
        // Next button creates a user profile and clears the EditText
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send user info on click
                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(mUserDepartment, mUserEmail, mUsername, mUserOrganization,
                            mUserPhoneNoEditText.getText().toString(), mPhotoUrl, mUserRole, mUsernameEditText.getText().toString());
                } else {
                    updateUser(mUserDepartment, null, null, mUserOrganization,
                            mUserPhoneNoEditText.getText().toString(), mPhotoUrl, mUserRole, mUsernameEditText.getText().toString());
                }
                // Clear input box
                mUsernameEditText.setText("");
                mUserPhoneNoEditText.setText("");
            }
        });

        if(usernameUpdaed && phoheUpdated){
            updateUI();
        }
    }

     /**
     * Creating new user node under 'users'
     */
    private void createUser(String department, String email, String name,
                            String organization, String phone, String photoUrl, String role, String username) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = uid;
        }

        User user = new User(department, email, name, organization, phone, photoUrl, role, username);

        mUsersDatabaseReference.child(userId).setValue(user);

        addUserChangeListener();
    }
    private void updateUser(String department, String email, String name,
                            String organization, String phone, String photoUrl, String role, String username) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(username)){
            mUsersDatabaseReference.child(userId).child("username").setValue(username).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    usernameUpdaed = true;
                }
            });
        }

        if (!TextUtils.isEmpty(phone)){
            mUsersDatabaseReference.child(userId).child("phone").setValue(phone).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    phoheUpdated = true;
                }
            });
        }
    }

    //TextWatcher for enabling next Button
    private TextWatcher userInfoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userIdInput = mUsernameEditText.getText().toString().trim();
            String userPhoneInput = mUserPhoneNoEditText.getText().toString().trim();

            mNextButton.setEnabled(!userIdInput.isEmpty() && !userPhoneInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // [START updateUI]
    private void updateUI() {
        Toast.makeText(this, "Please Select Course From\nSettings > Registration", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
    }
    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mUsersDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }
                updateUI();
                Log.e(TAG, "User data is changed!");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Please Try Again!", Toast.LENGTH_SHORT).show();
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void detachDatabaseReadListener() {
//        if (mValueEventListener != null) {
//            mCourseDatabaseReferance.removeEventListener(mValueEventListener);
//            mValueEventListener = null;
//        }
    }
}
