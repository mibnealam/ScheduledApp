package com.example.mibne.scheduledapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mibne.scheduledapp.Adapters.MainFragmentPagerAdapter;
import com.example.mibne.scheduledapp.Models.Routine;
import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static com.example.mibne.scheduledapp.Activities.LoginActivity.checkConnection;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "MainActivity";

    Menu nav_Menu;

    TextView navUserId;
    TextView navUserName;
    ImageView navUserPortrait;
    LinearLayout navUserContainer;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private LinearLayout mNoInternetView;

    SharedPreferences sharedPreferences;

    public static Bundle userDataBundle = new Bundle();

    private String uid;
    public static String role;

    private String RESULT_URL = "";

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ValueEventListener mUserValueEventListener;
    private ValueEventListener mValueEventListenerForRoutine;

    private DatabaseReference rootRef;
    private DatabaseReference mRoutineDatabaseReference;

    private List<String> enrolledCourses = new ArrayList<>();
    private List<Routine> routineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        rootRef = mFirebaseDatabase.getReference();

        findViewById(R.id.content_main).setVisibility(View.GONE);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mNoInternetView = (LinearLayout) findViewById(R.id.no_internet);
        mNoInternetView.setVisibility(View.GONE);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_manage_users).setVisible(false);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView =  navigationView.getHeaderView(0);
        navUserId = (TextView)headerView.findViewById(R.id.user_id_text_view);
        navUserName = (TextView)headerView.findViewById(R.id.user_name_text_view);
        navUserPortrait = (ImageView) headerView.findViewById(R.id.user_portrait_image_view);
        navUserContainer = (LinearLayout) headerView.findViewById(R.id.header_container_user_info);

        navUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditUserAccountActivity.class);
                intent.putExtras(userDataBundle);
                startActivity(intent);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.v("test", "User is signed in");
                    uid = user.getUid();
                    mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users/" + uid);
                    if (checkConnection(MainActivity.this)) {
                        onSignedInInitialize();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mNoInternetView.setVisibility(View.VISIBLE);
                        findViewById(R.id.content_main).setVisibility(View.GONE);
                    }
                } else {
                    // User is signed out
                    Log.v("test", "User is signed out");
                    onSignedOutCleanup();
                    //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        ValueEventListener urlRequestValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    RESULT_URL = dataSnapshot.getValue().toString();
                    Log.v("RESULT_URL", dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.child("RESULT_URL").addListenerForSingleValueEvent(urlRequestValueEventListener);

        mValueEventListenerForRoutine = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot routineDataSnapshot : dataSnapshot.getChildren()) {
                        Routine routine = routineDataSnapshot.getValue(Routine.class);
                        routineList.add(routine);
                    }
                    // Find the view pager that will allow the user to swipe between fragments
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

                    // Create an adapter that knows which fragment should be shown on each page
                    MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(MainActivity.this, getSupportFragmentManager());

                    // Set the adapter onto the view pager
                    viewPager.setAdapter(adapter);

                    // Give the TabLayout the ViewPager
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                    tabLayout.setupWithViewPager(viewPager);
                    findViewById(R.id.content_main).setVisibility(View.VISIBLE);
                    mNoInternetView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setText(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        if (sharedPreferences.getBoolean("isSubscriptionSet", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic(userDataBundle.getString("organization") + "General")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sharedPreferences.edit().putBoolean("isSubscribedToGeneral", true).apply();
                            Log.v("NotificationFailure", "Success > " + userDataBundle.getString("organization")+"General");
                            FirebaseMessaging.getInstance().subscribeToTopic(userDataBundle.getString("organization") + userDataBundle.getString("department"))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sharedPreferences.edit().putBoolean("isSubscribedToDept", true).apply();
                                            Log.v("NotificationFailure", "Success > " + userDataBundle.getString("organization")+userDataBundle.getString("department"));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("NotificationFailure", e.getMessage());
                                    sharedPreferences.edit().putBoolean("isSubscribedToDept", false).apply();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("NotificationFailure", e.getMessage());
                    sharedPreferences.edit().putBoolean("isSubscribedToGeneral", false).apply();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("ActivityLifecycle:", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("ActivityLifecycle:", "onResume");
        enrolledCourses.clear();
        routineList.clear();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("ActivityLifecycle:", "onPauseMainActivity");
        Log.v("ActivityLifecycle:", "RoutineList" + routineList.toString());
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        enrolledCourses.clear();
        routineList.clear();
        detachDatabaseReadListener();
    }
    long back_pressed;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            finish();
        } else{
            Toast.makeText(getBaseContext(),
                    "Press once again to exit!", Toast.LENGTH_SHORT)
                    .show();
        }
        back_pressed = System.currentTimeMillis();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notice) {
            Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
            intent.putExtras(userDataBundle);
            startActivity(intent);
        } else if (id == R.id.nav_routine) {
            Intent intent = new Intent(getApplicationContext(), RoutineActivity.class);
            intent.putExtras(userDataBundle);
            startActivity(intent);
        }
        else if (id == R.id.nav_result) {
            if (!RESULT_URL.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(RESULT_URL));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Sorry! Try again later.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.nav_manage_users) {
            Intent intent = new Intent(getApplicationContext(), ManageUsersActivity.class);
            intent.putExtras(userDataBundle);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtras(userDataBundle);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            FirebaseAuth.getInstance().signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onSignedInInitialize() {
        mUserValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("username").exists()
                        && dataSnapshot.child("department").exists()
                        && dataSnapshot.child("organization").exists()){
                    if(!dataSnapshot.child("courses").exists()){
                        showSelectCourseToast();
                    } else {
                        mRoutineDatabaseReference = mFirebaseDatabase.getReference().child(dataSnapshot.child("organization").getValue().toString() + "/" + dataSnapshot.child("department").getValue().toString() + "/routines");
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            if (snapshot.getKey().equals("courses")){
                                for (DataSnapshot snapshot1: snapshot.getChildren()){
                                    enrolledCourses.add(snapshot1.getKey());
                                }
                                break;
                            }
                        }
                        routineList.clear();
                        for (String courseCode: enrolledCourses) {
                            mRoutineDatabaseReference.orderByChild("courseCode").equalTo(courseCode).addListenerForSingleValueEvent(mValueEventListenerForRoutine);
                        }
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                    startActivity(intent);
                }

                if (dataSnapshot.exists()) {
                    Log.v(TAG, dataSnapshot.toString());
                    // Initialize user data variables with user data and
                    // pass them through required activity and fragments
                    navUserId.setText(dataSnapshot.child("username").getValue().toString());
                    navUserName.setText(dataSnapshot.child("name").getValue().toString());
                    try {
                        Glide.with(navUserPortrait).load(dataSnapshot.child("photoUrl").getValue()).into(navUserPortrait);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                    //Get Post object and use the values to update the UI
                    User user = dataSnapshot.getValue(User.class);

                    role = user.getRole();
                    if (role.equals("admin") || role.equals("super")) {
                        nav_Menu.findItem(R.id.nav_manage_users).setVisible(true);
                    }

                    userDataBundle.putString("uid", uid);
                    userDataBundle.putString("userName", user.getName());
                    userDataBundle.putString("userId", user.getUsername());
                    userDataBundle.putString("email", user.getEmail());
                    userDataBundle.putString("phone", user.getPhone());
                    userDataBundle.putString("photoUrl", user.getPhotoUrl());
                    userDataBundle.putString("organization", user.getOrganization());
                    userDataBundle.putString("department", user.getDepartment());
                    userDataBundle.putString("role", role);

                    sharedPreferences.edit().putString("uid", uid).apply();
                    sharedPreferences.edit().putString("userId", user.getUsername()).apply();
                    sharedPreferences.edit().putString("organization", user.getOrganization()).apply();
                    sharedPreferences.edit().putString("department", user.getDepartment()).apply();
                    sharedPreferences.edit().putString("role", role).apply();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Database Error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mUsersDatabaseReference.addValueEventListener(mUserValueEventListener);
    }

    private void showSelectCourseToast() {
        mProgressBar.setVisibility(View.GONE);
        mEmptyTextView.setText(R.string.prompt_no_selected_course);
        findViewById(R.id.content_main).setVisibility(View.GONE);
        Toast.makeText(this, "Please Select Course From\nSettings > Registration", Toast.LENGTH_LONG).show();
    }

    private void onSignedOutCleanup() {
        detachDatabaseReadListener();
    }


    private void detachDatabaseReadListener() {
//        if (mUserValueEventListener != null) {
//            mUsersDatabaseReference.removeEventListener(mUserValueEventListener);
//            mUserValueEventListener = null;
//        }
    }

    public List<Routine> getRoutineData() {
        return this.routineList;
    }
}
