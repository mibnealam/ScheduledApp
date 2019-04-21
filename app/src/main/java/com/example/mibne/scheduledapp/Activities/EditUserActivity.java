package com.example.mibne.scheduledapp.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mibne.scheduledapp.Activities.AddUserActivity.validateID;
import static com.example.mibne.scheduledapp.Activities.AddUserActivity.validatePhone;
import static com.example.mibne.scheduledapp.Activities.LoginActivity.checkConnection;
import static com.example.mibne.scheduledapp.Activities.LoginActivity.validateEmail;

public class EditUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String TAG = "EditUserActivity";

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
    private String mUserDepartment;

    private String mUserRole;
    private String mUserOrganization;

    private SharedPreferences sharedPreferences;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static DatabaseReference mUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Bundle bundle = getIntent().getExtras();


        name = bundle.getString("name");
        username = bundle.getString("id");
        email = bundle.getString("email");
        phone = bundle.getString("phone");
        role = bundle.getString("role");
        uid = bundle.getString("uid");
        mUserDepartment = bundle.getString("department");

        final List<String> departmentsKeysList = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        //final Bundle mNoticeTypeBundle = getIntent().getExtras();
        mUserRole = sharedPreferences.getString("role", "student");
        mUserOrganization = sharedPreferences.getString("organization", null);

        activity = this;

        getSupportActionBar().setTitle("Edit User");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = new User();

        rolesSpinner = (Spinner) findViewById(R.id.roles_spinner);

        final Spinner departmentSpinner = (Spinner) findViewById(R.id.departments_spinner);
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
                    for (String s: departmentsKeysList) {
                        if (s.equals(mUserDepartment)) {
                            departmentSpinner.setSelection(departmentsKeysList.indexOf(s));
                            departmentSpinnerAdapter.notifyDataSetChanged();
                        }
                    }
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
                user.setDepartment(parent.getItemAtPosition(position).toString());
                mUserDepartment = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       // departmentsKeysList.
        //Log.v("Department", user.getDepartment());
        departmentSpinner.setSelection(departmentsKeysList.indexOf(mUserDepartment));
        departmentSpinnerAdapter.notifyDataSetChanged();

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

    public static boolean validateName(TextInputLayout textInputLayout) {
        String nameInput = textInputLayout.getEditText().getText().toString().trim();

        if (nameInput.length() > 60) {
            textInputLayout.setError("Too long");
            return false;
        } else if (nameInput.length() < 5) {
            textInputLayout.setError("Too short");
            return false;
        }else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public boolean confirmInput() {
        if (!validateName(nameTextInputLayout) | !validateID(idTextInputLayout)
                | !validateEmail(emailTextInputLayout) | !validatePhone(phoneTextInputLayout)) {
            return false;
        } else {
            user.setName(nameTextInputLayout.getEditText().getText().toString().trim());
            user.setUsername(idTextInputLayout.getEditText().getText().toString().trim());
            user.setEmail(emailTextInputLayout.getEditText().getText().toString().trim());
            user.setPhone(phoneTextInputLayout.getEditText().getText().toString().trim());
            user.setRole(userRole);
            user.setDepartment(mUserDepartment);

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
            childUpdates.put("department", user.getDepartment());

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
