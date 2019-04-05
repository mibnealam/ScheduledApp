package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static Activity activity;

    private User user;

    private TextInputLayout nameTextInputLayout;
    private TextInputLayout idTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    //private TextInputLayout roleTextInputLayout;

    private Spinner rolesSpinner;

    private static String name;
    private static String username;
    private String email;
    private String phone;
    private String role;
    private String userRole;
    private static String uid;

    private FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        activity = this;

        getSupportActionBar().setTitle("Edit User");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = new User();

        rolesSpinner = (Spinner) findViewById(R.id.roles_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        rolesSpinner.setAdapter(adapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

        Bundle bundle = getIntent().getExtras();


        name = bundle.getString("name");
        username = bundle.getString("id");
        email = bundle.getString("email");
        phone = bundle.getString("phone");
        role = bundle.getString("role");
        uid = bundle.getString("uid");

        nameTextInputLayout = (TextInputLayout) findViewById(R.id.edit_name_wrapper);
        idTextInputLayout = (TextInputLayout) findViewById(R.id.edit_id_wrapper);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.edit_email_wrapper);
        emailTextInputLayout.setEnabled(false);
        phoneTextInputLayout = (TextInputLayout) findViewById(R.id.edit_phone_wrapper);
        //roleTextInputLayout = (TextInputLayout) findViewById(R.id.edit_role_wrapper);

        nameTextInputLayout.getEditText().setText(name);
        idTextInputLayout.getEditText().setText(username);
        emailTextInputLayout.getEditText().setText(email);
        phoneTextInputLayout.getEditText().setText(phone);
        //roleTextInputLayout.getEditText().setText(role);

        Button button = (Button) findViewById(R.id.button_save_user_info);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            updateInfo();
            }
        });

        rolesSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_routine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete:
                openDeleteUserConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        if (emailInput.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
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

//    private boolean validateRole() {
//        String roleInput = roleTextInputLayout.getEditText().getText().toString().trim();
//
//        if (roleInput.isEmpty()) {
//            roleTextInputLayout.setError("Empty.");
//            return false;
//        } else if (roleInput.length() > 10) {
//            roleTextInputLayout.setError("Too long");
//            return false;
//        } else if (roleInput.length() < 5) {
//            roleTextInputLayout.setError("Too short");
//            return false;
//        } else {
//            roleTextInputLayout.setError(null);
//            return true;
//        }
//    }

    public boolean confirmInput() {
        if (!validateName() | !validateID() | !validateEmail() | !validatePhone()) {
            return false;
        } else {
            user.setName(nameTextInputLayout.getEditText().getText().toString().trim());
            user.setUsername(idTextInputLayout.getEditText().getText().toString().trim());
            user.setEmail(emailTextInputLayout.getEditText().getText().toString().trim());
            user.setPhone(phoneTextInputLayout.getEditText().getText().toString().trim());
            //user.setRole(roleTextInputLayout.getEditText().getText().toString().trim());
            user.setRole(userRole);

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
            childUpdates.put("role", user.getRole());

            if (uid != null) {
                mUserDatabaseReference.child(uid).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditUserActivity.this, "Account Info Saved Successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please try again later!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Deletes a routine from database
     */
    public static class confirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setTitle(R.string.title_dialog_confirm);
            builder.setMessage(R.string.prompt_dialog_confirm_user)
                    .setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // delete routine from FireBase

                            mUserDatabaseReference.child(uid).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(activity, "Deletion of " + username + "  is complete", Toast.LENGTH_LONG).show();
                                    activity.finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, "Deletion of " + username + "  is failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public void openDeleteUserConfirmationDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new confirmDialogFragment();
        dialog.show(getSupportFragmentManager(), "confirmDeletion");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        userRole = (String) parent.getItemAtPosition(position);
        Log.v("userRole:", userRole);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
