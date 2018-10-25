package com.example.mibne.scheduledapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "MainActivity";

    TextView navUserId;
    TextView navUserName;
    ImageView navUserPortrait;
    LinearLayout navUserContainer;

    Bundle userDataBundle = new Bundle();

    String uid;
    String userName;
    String userID;
    String userOrganization;
    String userDepartment;
    List<Course> userCourses;
    String userEmail;
    String userPhone;

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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


        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.v("test", "User is signed in");
                    uid = user.getUid();
                    mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users/" + uid);
                    onSignedInInitialize(mUsersDatabaseReference);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
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
            intent.putExtra("Type", "Notice");
            startActivity(intent);
        } else if (id == R.id.nav_routine) {
            Intent intent = new Intent(getApplicationContext(), RoutineActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_assignment) {
            Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
            intent.putExtra("Type", "Assignment");
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

    private void onSignedInInitialize(DatabaseReference userinfo) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, dataSnapshot.toString());
                //Todo : initialize user data variables with user data and
                //Todo : pass them through required activity and fragments
                navUserId.setText(dataSnapshot.child("username").getValue().toString());
                navUserName.setText(dataSnapshot.child("name").getValue().toString());
                Glide.with(navUserPortrait).load(dataSnapshot.child("photoUrl").getValue()).into(navUserPortrait);
                //Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);

                userDataBundle.putString("uid", uid);
                userDataBundle.putString("userName", user.getName());
                userDataBundle.putString("userId", user.getUsername());
                userDataBundle.putString("email", user.getEmail());
                userDataBundle.putString("phone", user.getPhone());
                userDataBundle.putString("photoUrl", user.getPhotoUrl());
                userDataBundle.putString("organization", user.getOrganization());
                userDataBundle.putString("department", user.getDepartment());
                userDataBundle.putString("role", user.getRole());

                Log.v("userName", userDataBundle.toString());

                if(dataSnapshot.child("username").exists() && dataSnapshot.child("phone").exists()){
                    if(!dataSnapshot.child("courses").exists()){
                        Log.v("test", "no course selected!");
                        //Todo if there is no course selected.
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Database Error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        userinfo.addListenerForSingleValueEvent(userListener);
    }

    private void onSignedOutCleanup() {
        detachDatabaseReadListener();
    }


    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
