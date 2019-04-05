package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String TAG = "AddUserActivity";
    private static Activity activity;

    private User user;

    private TextInputLayout nameTextInputLayout;
    private TextInputLayout idTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    //private TextInputLayout roleTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;

    private Spinner rolesSpinner;

    private boolean passwordMatched = false;

    private static String name;
    private static String username;
    private String email;
    private String password;
    private String phone;
    private String userRole;

    private String mUserDepartment;
    private String mUserOrganization;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        getSupportActionBar().setTitle("Add User");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle userDataBundle = getIntent().getExtras();
        mUserOrganization = userDataBundle.getString("organization");
        mUserDepartment = userDataBundle.getString("department");

        user = new User();

        nameTextInputLayout = (TextInputLayout) findViewById(R.id.edit_name_wrapper);
        idTextInputLayout = (TextInputLayout) findViewById(R.id.edit_id_wrapper);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.edit_email_wrapper);
        phoneTextInputLayout = (TextInputLayout) findViewById(R.id.edit_phone_wrapper);
        //roleTextInputLayout = (TextInputLayout) findViewById(R.id.edit_role_wrapper);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.user_password_wrapper);
        confirmPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.confirm_password_wrapper);

        rolesSpinner = (Spinner) findViewById(R.id.roles_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        rolesSpinner.setAdapter(adapter);

        rolesSpinner.setOnItemSelectedListener(this);


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

        confirmPasswordTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strPass1 = passwordTextInputLayout.getEditText().getText().toString();
                String strPass2 = confirmPasswordTextInputLayout.getEditText().getText().toString();
                if (strPass1.equals(strPass2)) {
                    confirmPasswordTextInputLayout.setError(null);
                    passwordMatched = true;
                } else {
                    confirmPasswordTextInputLayout.setError(" ");
                    passwordMatched = false;
                }
            }
        });

        Button button = (Button) findViewById(R.id.button_add_user);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateName() {
        String nameInput = nameTextInputLayout.getEditText().getText().toString().trim();

        if (nameInput.length() > 60) {
            nameTextInputLayout.setError("Too long");
            return false;
        } else if (nameInput.isEmpty()) {
            nameTextInputLayout.setError("Empty field");
            return false;
        }else {
            nameTextInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateID() {
        String userIDInput = idTextInputLayout.getEditText().getText().toString().trim();

        if (userIDInput.isEmpty()) {
            idTextInputLayout.setError("Please set room number.");
            return false;
        } else if (userIDInput.length() > 14) {
            idTextInputLayout.setError("Too long");
            return false;
        } else if (userIDInput.length() < 3) {
            idTextInputLayout.setError("Too short");
            return false;
        } else {
            idTextInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = emailTextInputLayout.getEditText().getText().toString().trim();

        if (emailInput.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\" +
                ".[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d" +
                "-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:" +
                "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]" +
                "|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21" +
                "-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            emailTextInputLayout.setError(null);
            return true;
        } else {
            emailTextInputLayout.setError("Invalid email!");
            return false;
        }
    }

    private boolean validatePhone() {
        String phoneInput = phoneTextInputLayout.getEditText().getText().toString().trim();

        if (phoneInput.matches("[+]|[8]{2}|[0][1][3|5-9][0-9]{8}")) {
            phoneTextInputLayout.setError(null);
            return true;
        } else {
            phoneTextInputLayout.setError("Invalid number!");
            return false;
        }
    }

    // [START validateUserPassword]
    private boolean validateUserPassword() {
        String userPasswordInput = passwordTextInputLayout.getEditText().getText().toString().trim();
        if (userPasswordInput.length() < 6) {
            passwordTextInputLayout.setError("short");
            return false;
        } else {
            passwordTextInputLayout.setError(null);
            password = userPasswordInput;
            return true;
        }
    }
    // [END validateUserPassword]

    public boolean confirmInput() {
        if (!validateName() | !validateID() | !validateEmail() | !validatePhone() | !validateUserPassword() | !passwordMatched) {
            return false;
        } else {
            user.setName(nameTextInputLayout.getEditText().getText().toString().trim());
            user.setUsername(idTextInputLayout.getEditText().getText().toString().trim());
            user.setEmail(emailTextInputLayout.getEditText().getText().toString().trim());
            user.setPhone(phoneTextInputLayout.getEditText().getText().toString().trim());
            user.setRole(userRole);
            email = user.getEmail();

            return true;
        }
    }

    private void addUser() {
        if (confirmInput()) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                mUserDatabaseReference.child(firebaseUser.getUid()).setValue(new User(mUserDepartment, user.getEmail(), user.getName(), mUserOrganization, user.getPhone(), "", user.getRole(), user.getUsername(), firebaseUser.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddUserActivity.this, "User data not saved.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //updateUI(null);
                                try
                                {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthWeakPasswordException weakPassword)
                                {
                                    Log.d(TAG, "onComplete: weak_password");

                                    // TODO: take your actions!
                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    Log.d(TAG, "onComplete: malformed_email");

                                    // TODO: Take your action
                                }
                                catch (FirebaseAuthUserCollisionException existEmail)
                                {
                                    Log.d(TAG, "onComplete: exist_email");
                                    Toast.makeText(AddUserActivity.this, "Email exists.",
                                            Toast.LENGTH_SHORT).show();

                                    // TODO: Take your action
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                }
                            }

                            // ...
                        }
                    });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        userRole = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}