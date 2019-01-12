package com.example.mibne.scheduledapp;

        import android.app.Activity;
        import android.support.annotation.NonNull;
        import android.support.design.widget.TextInputLayout;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity {

    String TAG = "AddUserActivity";
    private static Activity activity;

    private User user;

    private TextInputLayout nameTextInputLayout;
    private TextInputLayout idTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout roleTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;

    private boolean passwordMatched = false;

    private static String name;
    private static String username;
    private String email;
    private String password;
    private String phone;
    private String role;

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
        roleTextInputLayout = (TextInputLayout) findViewById(R.id.edit_role_wrapper);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.user_password_wrapper);
        confirmPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.confirm_password_wrapper);


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
        } else if (nameInput.length() < 5) {
            nameTextInputLayout.setError("Too short");
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

        if (emailInput.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
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

    private boolean validateRole() {
        String roleInput = roleTextInputLayout.getEditText().getText().toString().trim();

        if (roleInput.isEmpty()) {
            roleTextInputLayout.setError("Please set room number.");
            return false;
        } else if (roleInput.length() > 10) {
            roleTextInputLayout.setError("Too long");
            return false;
        } else if (roleInput.length() < 5) {
            roleTextInputLayout.setError("Too short");
            return false;
        } else {
            roleTextInputLayout.setError(null);
            return true;
        }
    }

    // [START validateUserPassword]
    private boolean validateUserPassword() {
        String userPasswordInput = passwordTextInputLayout.getEditText().getText().toString().trim();
        if (userPasswordInput.length() < 8) {
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
        if (!validateName() | !validateID() | !validateEmail() | !validatePhone() | !validateRole() | !validateUserPassword() | !passwordMatched) {
            return false;
        } else {
            user.setName(nameTextInputLayout.getEditText().getText().toString().trim());
            user.setUsername(idTextInputLayout.getEditText().getText().toString().trim());
            user.setEmail(emailTextInputLayout.getEditText().getText().toString().trim());
            user.setPhone(phoneTextInputLayout.getEditText().getText().toString().trim());
            user.setRole(roleTextInputLayout.getEditText().getText().toString().trim());
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
                                mUserDatabaseReference.child(firebaseUser.getUid()).setValue(user);
                                //updateUI(user);
                                FirebaseAuth.getInstance().signOut();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AddUserActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }
}