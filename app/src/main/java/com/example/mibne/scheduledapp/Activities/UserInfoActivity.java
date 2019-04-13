package com.example.mibne.scheduledapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
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

import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.User;
import com.example.mibne.scheduledapp.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity  {

    private static final String TAG = "UserInfoActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_USERNAME_LENGTH_LIMIT = 30;
    public static final int DEFAULT_PHONE_NO_LENGTH_LIMIT = 11;

    /** URL for earthquake data from the USGS dataset */
    private static final String USSER_REQUEST_URL =
            "http://103.239.5.178:2020/api/index.php?Id=";

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

    private boolean isValidUserName = false;
    private boolean isValidUserPhone = false;
    private boolean usernameUpdated = false;
    private boolean phoneUpdated = false;
    private boolean isValidUser = false;


    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mUserOrganization ="0";
        mUserDepartment = "Select your department";

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mUsersDatabaseReference = mDatabaseReference.child("users");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mUsernameEditText = (EditText) findViewById(R.id.user_id);
        mUserPhoneNoEditText = (EditText) findViewById(R.id.user_phone_no);
        mNextButton = (Button) findViewById(R.id.next_button);

        final List<String> userIdList = new ArrayList<>();
        /**
         * Get Users info
         */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            try {
                if (!user.getDisplayName().isEmpty()) {
                    mUsername = user.getDisplayName();
                } else {
                    mUsername = ANONYMOUS;
                }
            } catch (Exception e) {
                mUsername = ANONYMOUS;
            }
            mUserEmail = user.getEmail();
            try {
                Uri photoUrl = user.getPhotoUrl();
                mPhotoUrl = photoUrl.toString();
            } catch (Exception e) {
                mPhotoUrl = "";
            }


            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
        }

        mUserRole = "student";
        //Get organizations
        final List<String> organizationNamesList = new ArrayList<>();
        final List<String> organizationKeysList = new ArrayList<>();

        final List<String> departmentsKeysList = new ArrayList<>();


        Spinner organizationSpinner = (Spinner) findViewById(R.id.organizations_spinner);
        Spinner departmentSpinner = (Spinner) findViewById(R.id.departments_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> organizationSpinnerAdapter;
        organizationSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, organizationNamesList);

        final ArrayAdapter<String> departmentSpinnerAdapter;
        departmentSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departmentsKeysList);
        // Specify the layout to use when the list of choices appears
        organizationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        organizationSpinner.setAdapter(organizationSpinnerAdapter);
        departmentSpinner.setAdapter(departmentSpinnerAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        ValueEventListener mUserValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                organizationKeysList.clear();
                organizationNamesList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        organizationNamesList.add(snapshot.getValue().toString());
                        organizationKeysList.add(snapshot.getKey());
                    }
                    organizationSpinnerAdapter.notifyDataSetChanged();
                } else {
                    Log.v("DataOrganization", "Does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Database Error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabaseReference.child("organizations").addValueEventListener(mUserValueEventListener);

        final ValueEventListener departmentListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    departmentsKeysList.clear();
                    departmentsKeysList.add("Select your department");
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("notices")) {
                            departmentsKeysList.add(snapshot.getKey());
                        }
                        //Organization organization = snapshot.getValue(Organization.class);
                        //organizationList.add(organization.getName());
                    }
                    departmentSpinnerAdapter.notifyDataSetChanged();
                } else {
                    Log.v(TAG, "No snapshot of department");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };


        // Enable Next button when there's information
        mUsernameEditText.addTextChangedListener(userInfoTextWatcher);
        mUserPhoneNoEditText.addTextChangedListener(userInfoTextWatcher);
        mUsernameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_USERNAME_LENGTH_LIMIT)});
        mUserPhoneNoEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_PHONE_NO_LENGTH_LIMIT)});

        organizationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                departmentsKeysList.clear();
                departmentSpinnerAdapter.notifyDataSetChanged();
                mUserOrganization = organizationKeysList.get(position);
                Log.v("SelectedOrg", mUserOrganization);
                mDatabaseReference.child(organizationKeysList.get(position)).addListenerForSingleValueEvent(departmentListener);
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
                Log.v("selectedDept:", mUserDepartment);
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
                if (isValidUserId(userIdList, mUsernameEditText.getText().toString())) {
                    UserExistsAsyncTask task = new UserExistsAsyncTask();
                    task.execute(USSER_REQUEST_URL + userId);
                } else {
                    Toast.makeText(UserInfoActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        userIdList.add(user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUsersDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void updateUser(String department, String email, String name,
                            String organization, String phone, String photoUrl, String role, String username, String uid) {
        // updating the user via child nodes
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("name", name);
        childUpdates.put("email", email);
        childUpdates.put("organization", organization);
        childUpdates.put("department", department);
        childUpdates.put("phone", phone);
        childUpdates.put("photoUrl", photoUrl);
        childUpdates.put("username", username);
        childUpdates.put("role", role);
        childUpdates.put("uid", uid);

        mUsersDatabaseReference.child(uid).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateUI();
                Toast.makeText(UserInfoActivity.this, "Account Info Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
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
            if (userIdInput.isEmpty()) {
                mUsernameEditText.setError("Empty field");
                isValidUserName = false;
            } else {
                userId = userIdInput;
                isValidUserName = true;
                mUsernameEditText.setError(null);
            }
            if (userPhoneInput.isEmpty()) {
                mUserPhoneNoEditText.setError("Empty field");
                isValidUserPhone = false;
            } else {
                if (userPhoneInput.matches("[+]|[8]{2}|[0][1][1-9][0-9]{8}")) {
                    isValidUserPhone = true;
                    mUserPhoneNoEditText.setError(null);
                } else {
                    mUserPhoneNoEditText.setError("Invalid number!");
                }
            }
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

    private void detachDatabaseReadListener() {
//        if (mValueEventListener != null) {
//            mCourseDatabaseReferance.removeEventListener(mValueEventListener);
//            mValueEventListener = null;
//        }
    }


    private boolean isValidUserId (List<String> list, String userId) {
        for (String s: list) {
            if (s.equals(userId)) {
                return false;
            }
        }
        return true;
    }
    private class UserExistsAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request.
         *
         * It is NOT okay to update the UI from a background thread, so we just return an
         * {@link boolean} object as the result.
         */
        @Override
        protected Boolean doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return false;
            }

            boolean result = Utils.fetchUserData(urls[0]);
            return result;
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         *
         * It IS okay to modify the UI within this method. We take the {@link boolean} value
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */
        @Override
        protected void onPostExecute(Boolean result) {
            // If there is no result, do nothing.
            if (result) {
                mProgressBar.setVisibility(View.GONE);
                if (!mUserOrganization.equals("0")
                        && !mUserDepartment.equals("Select your department")){
                    updateUser(mUserDepartment, mUserEmail, mUsername, mUserOrganization,
                            mUserPhoneNoEditText.getText().toString(),
                            mPhotoUrl, mUserRole, mUsernameEditText.getText().toString(), uid);
                } else {
                    Toast.makeText(UserInfoActivity.this,
                            "Fill up the form correctly.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(UserInfoActivity.this,
                        "Invalid User Id.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
