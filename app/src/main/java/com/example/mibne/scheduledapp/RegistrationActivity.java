package com.example.mibne.scheduledapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {
    private ListView mCourseListView;
    private CourseAdapter mCourseAdapter;
    private ProgressBar mProgressBar;
    private String mUsername;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCourseDatabaseReferance;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mCourseDatabaseReferance = mFirebaseDatabase.getReference().child("courses");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mCourseListView = (ListView) findViewById(R.id.course_list_view);

        // Initialize course ListView and its adapter
        List<Course> courses = new ArrayList<>();
        mCourseAdapter = new CourseAdapter(this, R.layout.list_item_course, courses);
        mCourseListView.setAdapter(mCourseAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        attachDatabaseReadListener();
    }


    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Course course = dataSnapshot.getValue(Course.class);
                    mCourseAdapter.add(course);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mCourseDatabaseReferance.addChildEventListener(mChildEventListener);
        }
    }
    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mCourseDatabaseReferance.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
