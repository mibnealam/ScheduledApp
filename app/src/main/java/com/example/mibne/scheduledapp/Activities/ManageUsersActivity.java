package com.example.mibne.scheduledapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.User;
import com.example.mibne.scheduledapp.Adapters.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.view.View.INVISIBLE;

public class ManageUsersActivity extends AppCompatActivity implements UserAdapter.UserAdapterListener {

    private String TAG = "ManageUsersActivity";

    private static final int READ_REQUEST_CODE = 42;

    private static List<User> userList = new ArrayList<>();
    private List<User> uploadUserList = new ArrayList<>();

    private String mUserDepartment;
    private String mUserOrganization;
    private String role;

    private RecyclerView mUserRecyclerView;
    private UserAdapter mUserAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private SearchView searchView;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mUsersDatabaseReference;
    private ValueEventListener mValueEventListener;

    private String loginEmail;
    private String loginPassword;

    private boolean isConnected;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        loginEmail = sharedPreferences.getString("loginEmail", null);
        loginPassword = sharedPreferences.getString("loginPassword", null);

//        getSupportActionBar().setTitle("Manage Users");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final Bundle userDataBundle = getIntent().getExtras();
        mUserOrganization = userDataBundle.getString("organization");
        mUserDepartment = userDataBundle.getString("department");
        role = userDataBundle.getString("role");


        com.getbase.floatingactionbutton.FloatingActionsMenu floatingActionsMenu = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.fab_user);

        com.getbase.floatingactionbutton.FloatingActionButton floatingActionButtonRemoveUsers = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_remove_user);

        floatingActionButtonRemoveUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteUsersConfirmationDialog();

            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton floatingActionButtonUploadRoutine = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_upload_user);

        floatingActionButtonUploadRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();

            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton floatingActionButtonAddRoutine = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_add_user);

        floatingActionButtonAddRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddUserActivity.class);
                intent.putExtras(userDataBundle);
                startActivity(intent);
            }
        });

        switch (role) {
            case "teacher" :
                floatingActionsMenu.setVisibility(View.VISIBLE);
                floatingActionButtonUploadRoutine.setVisibility(View.GONE);
                checkFilePermissions();
                break;
            case "admin" :
                floatingActionsMenu.setVisibility(View.VISIBLE);
                checkFilePermissions();
                break;
            default:
                floatingActionsMenu.setVisibility(View.GONE);
                break;
        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");


        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_user_list);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view_user_list);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mUserRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_user);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mUserRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mUserRecyclerView.setHasFixedSize(true);
        /*
         * The UserAdapter is responsible for linking our course data with the Views that
         * will end up displaying user data.
         */
        mUserAdapter = new UserAdapter(this, userList, this);

        mUserRecyclerView.setAdapter(mUserAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    public void openDeleteUsersConfirmationDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new confirmDialogFragment();
        dialog.show(getSupportFragmentManager(), "confirmDeletion");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mUserAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mUserAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        userList.clear();
        attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        userList.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    readExcelData(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void attachDatabaseReadListener() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userDataSnapshot: dataSnapshot.getChildren()) {
                        User user =  userDataSnapshot.getValue(User.class);
                        if (user.getDepartment().equals(mUserDepartment) && !role.equals("super")
                        && !user.getRole().equals("super")) {
                            userList.add(user);
                        }

                        if (role.equals("super") && user.getRole().equals("admin")) {
                            userList.add(user);
                        }
                    }
                    mUserAdapter.notifyDataSetChanged();
                    mEmptyTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(INVISIBLE);
                } else {
                    mProgressBar.setVisibility(INVISIBLE);
                    mEmptyTextView.setText(R.string.prompt_no_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUsersDatabaseReference.addValueEventListener(mValueEventListener);
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    public void readExcelData(Uri uri) throws IOException  {
        Workbook w;
        try {
            InputStream inputStream = getInputUri(uri);
            w = Workbook.getWorkbook(inputStream);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 1; j < sheet.getRows(); j++) {
                for (int i = 0; i < sheet.getColumns(); i++) {
                    jxl.Cell cell = sheet.getCell(i, j);
                    stringBuilder.append(cell.getContents() + ",");

                }
                stringBuilder.append(":");
            }
            Log.v("TestData", stringBuilder.toString());
            parseStringBuilder(stringBuilder);
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for parsing imported data and storing in ArrayList<XYValue>
     */
    public void parseStringBuilder(StringBuilder mStringBuilder){
        Log.d(TAG, "parseStringBuilder: Started parsing.");

        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(":");
        Log.v("TestData", String.valueOf(rows.length));

        //Add to the ArrayList<XYValue> row by row
        for(int i=0; i<rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split(",");

            //use try catch to make sure there are no "" that try to parse into doubles.
            try{
                final String username = (columns[0]);
                final String name = (columns[1]);
                final String email = (columns[2]);
                String password = (columns[3]);
                final String phone = (columns[4]);
                final String role = (columns[5]);

                String cellInfo = "(cell_info): (" + username + "," + name + "," + email + "," + name + "," + phone + "," + role  + ")";
                Log.d(TAG, "ParseStringBuilder: Data from row: " + cellInfo);

                uploadUserList.add(new User(mUserDepartment, email, name, mUserOrganization, phone, "", role, username, "_"));

                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                    mUsersDatabaseReference.child(user.getUid())
                                            .setValue(new User(mUserDepartment, email, name, mUserOrganization, phone, "", role, username, user.getUid()));
                                    //updateUI(user);
                                    FirebaseAuth.getInstance().signOut();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    try
                                    {
                                        throw task.getException();
                                    }
                                    // if user enters wrong email.
                                    catch (FirebaseAuthWeakPasswordException weakPassword)
                                    {
                                        Log.d(TAG, "onComplete: weak_password");

                                        Toast.makeText(ManageUsersActivity.this, "Weak password: " + email,
                                                Toast.LENGTH_LONG).show();
                                    }
                                    // if user enters wrong password.
                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                    {
                                        Log.d(TAG, "onComplete: malformed_email");

                                        Toast.makeText(ManageUsersActivity.this, "Malformed email: " + email,
                                                Toast.LENGTH_LONG).show();
                                    }
                                    catch (FirebaseAuthUserCollisionException existEmail)
                                    {
                                        Log.d(TAG, "onComplete: exist_email");
                                        Toast.makeText(ManageUsersActivity.this, "Email exists: " + email,
                                                Toast.LENGTH_LONG).show();
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(TAG, "onComplete: " + e.getMessage());
                                    }
                                }

                                // ...
                                logIn();
                            }
                        });

            }catch (NumberFormatException e){

                Log.e(TAG, "parseStringBuilder: NumberFormatException: " + e.getMessage());

            }
        }
    }



    public boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            return true;
        } else {
            return false;
        }

    }
    private void logIn() {
        if (checkConnection()) {
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
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.prompt_no_internet_connection, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an xls file.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only xls files, using the xls MIME data type.
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * Checks file permissions
     * If there is no file read permission then this method will make a request.
     */

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
                }
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
    private InputStream getInputUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        return inputStream;
    }

    @Override
    public void onUserSelected(User user) {
        Intent editUserIntent = new Intent(this, EditUserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", user.getName());
                    bundle.putString("id", user.getUsername());
                    bundle.putString("email", user.getEmail());
                    bundle.putString("phone", user.getPhone());
                    bundle.putString("role", user.getRole());
                    bundle.putString("uid", user.getUid());
                    editUserIntent.putExtras(bundle);
                    startActivity(editUserIntent);
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
            builder.setMessage(R.string.prompt_dialog_confirm_remove_users)
                    .setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // delete routine from FireBase
                            for (final User user: userList){
                                if (user.getRole().equals("student")) {
                                    mUsersDatabaseReference.child(user.getUid()).setValue(null).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Failed to delete " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

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
}
