package com.example.mibne.scheduledapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.User;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.mibne.scheduledapp.Activities.LoginActivity.checkConnection;
import static com.example.mibne.scheduledapp.Activities.LoginActivity.validateEmail;
import static com.example.mibne.scheduledapp.Activities.LoginActivity.validateUserPassword;

public class AddUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String TAG = "AddUserActivity";

    private User user;

    private TextInputLayout nameTextInputLayout;
    private TextInputLayout idTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
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
    private String mUserRole;

    private SharedPreferences  sharedPreferences;


    private String loginEmail;
    private String loginPassword;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static DatabaseReference mUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);


        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);
        loginEmail = sharedPreferences.getString("loginEmail", null);
        loginPassword = sharedPreferences.getString("loginPassword", null);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

        final List<String> departmentsKeysList = new ArrayList<>();

        getSupportActionBar().setTitle("Add User");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle userDataBundle = getIntent().getExtras();
        mUserOrganization = userDataBundle.getString("organization");
        mUserDepartment = userDataBundle.getString("department");
        mUserRole = userDataBundle.getString("role");

        user = new User();

        nameTextInputLayout = (TextInputLayout) findViewById(R.id.edit_name_wrapper);
        idTextInputLayout = (TextInputLayout) findViewById(R.id.edit_id_wrapper);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.edit_email_wrapper);
        phoneTextInputLayout = (TextInputLayout) findViewById(R.id.edit_phone_wrapper);
        //roleTextInputLayout = (TextInputLayout) findViewById(R.id.edit_role_wrapper);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.user_password_wrapper);
        confirmPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.confirm_password_wrapper);

        rolesSpinner = (Spinner) findViewById(R.id.roles_spinner);

        rolesSpinner.setOnItemSelectedListener(this);


        Spinner departmentSpinner = (Spinner) findViewById(R.id.departments_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout

        final ArrayAdapter<String> departmentSpinnerAdapter;
        departmentSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departmentsKeysList);
        // Specify the layout to use when the list of choices appears
        departmentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        departmentSpinner.setAdapter(departmentSpinnerAdapter);

        final ValueEventListener departmentListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    departmentsKeysList.clear();
                    departmentsKeysList.add("Select department");
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

        findViewById(R.id.container_department).setVisibility(View.GONE);
        if (mUserRole.equals("super")) {
            mDatabaseReference.child(mUserOrganization).addListenerForSingleValueEvent(departmentListener);
            findViewById(R.id.container_department).setVisibility(View.VISIBLE);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.roles_array_super, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            rolesSpinner.setAdapter(adapter);
        } else {
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.roles_array_executive, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            rolesSpinner.setAdapter(adapter);
        }

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

    public static boolean validateName(TextInputLayout textInputLayout) {
        String nameInput = textInputLayout.getEditText().getText().toString().trim();

        if (nameInput.length() > 60) {
            textInputLayout.setError("Too long");
            return false;
        } else if (nameInput.isEmpty()) {
            textInputLayout.setError("Empty field");
            return false;
        }else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static boolean validateID(TextInputLayout textInputLayout) {
        String userIDInput = textInputLayout.getEditText().getText().toString().trim();

        if (userIDInput.isEmpty()) {
            textInputLayout.setError("Please set room number.");
            return false;
        } else if (userIDInput.length() > 14) {
            textInputLayout.setError("Too long");
            return false;
        } else if (userIDInput.length() < 3) {
            textInputLayout.setError("Too short");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static boolean validatePhone(TextInputLayout textInputLayout) {
        String phoneInput = textInputLayout.getEditText().getText().toString().trim();

        if (phoneInput.matches("[+]|[8]{2}|[0][1][1-9][0-9]{8}")) {
            textInputLayout.setError(null);
            return true;
        } else {
            textInputLayout.setError("Invalid number!");
            return false;
        }
    }

    public boolean confirmInput() {
        if (!validateName(nameTextInputLayout) | !validateID(idTextInputLayout)
                | !validateEmail(emailTextInputLayout) | !validatePhone(phoneTextInputLayout)
                | !validateUserPassword(passwordTextInputLayout) | !passwordMatched) {
            return false;
        } else {
            user.setName(nameTextInputLayout.getEditText().getText().toString().trim());
            user.setUsername(idTextInputLayout.getEditText().getText().toString().trim());
            user.setEmail(emailTextInputLayout.getEditText().getText().toString().trim());
            user.setPhone(phoneTextInputLayout.getEditText().getText().toString().trim());
            password = passwordTextInputLayout.getEditText().getText().toString().trim();
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
                                mUserDatabaseReference.child(firebaseUser.getUid())
                                        .setValue(new User(mUserDepartment, user.getEmail()
                                                , user.getName(), mUserOrganization, user.getPhone()
                                                , "", user.getRole(), user.getUsername()
                                                , firebaseUser.getUid()))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseAuth.getInstance().signOut();
                                        logIn();
                                        Toast.makeText(AddUserActivity.this
                                                , "User added successfully."
                                                , Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddUserActivity.this
                                                , "User data not saved."
                                                , Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(AddUserActivity.this
                                            , "Weak Password.", Toast.LENGTH_SHORT).show();
                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    Log.d(TAG, "onComplete: malformed_email");

                                    Toast.makeText(AddUserActivity.this
                                            , "Malformed email", Toast.LENGTH_SHORT).show();
                                }
                                catch (FirebaseAuthUserCollisionException existEmail)
                                {
                                    Log.d(TAG, "onComplete: exist_email");
                                    Toast.makeText(AddUserActivity.this
                                            , "Email exists.", Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                }
                            }
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

    public void logIn() {
        if (checkConnection(this)) {
            mFirebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                            }
                        }
                    });
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content)
                    , R.string.prompt_no_internet_connection, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}