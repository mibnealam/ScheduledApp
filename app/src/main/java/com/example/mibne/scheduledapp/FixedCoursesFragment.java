package com.example.mibne.scheduledapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayFragment} interface
 * to handle interaction events.
 * Use the {@link AllCoursesFragment#} factory method to
 * create an instance of this fragment.
 */
public class FixedCoursesFragment extends Fragment {

    private List<Course> courseList = new ArrayList<>();

    private RecyclerView mCourseRecyclerView;
    private CourseAdapter mCourseAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private String uid;
    private String mUserOrganization;
    private String mUserDepartment;
    private String mUserBatch;


    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCourseDatabaseReferance;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;


    public FixedCoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.course_list, container, false);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uid = user.getUid();
        } else {
            // No user is signed in
        }

        mUserOrganization = "sub";
        mUserDepartment = "cse";
        mUserBatch = "40";

        mCourseDatabaseReferance = mFirebaseDatabase.getReference().child( mUserOrganization + "/" + mUserDepartment + "/" + mUserBatch + "/fixedCourses");

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_course_list);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_view_course_list);
        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mCourseRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_course);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCourseRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mCourseRecyclerView.setHasFixedSize(true);
        /*
         * The CourseAdapter is responsible for linking our course data with the Views that
         * will end up displaying our course data.
         */
        mCourseAdapter = new CourseAdapter();

        mCourseRecyclerView.setAdapter(mCourseAdapter);

        // Initialize course ListView and its adapter

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        attachDatabaseReadListener();

        return rootView;
    }

    private void attachDatabaseReadListener() {

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgressBar.setVisibility(INVISIBLE);
                } else {
                    mProgressBar.setVisibility(INVISIBLE);
                    mEmptyTextView.setText(R.string.prompt_no_course);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mCourseDatabaseReferance.addListenerForSingleValueEvent(mValueEventListener);

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()){
                        Course courses =  dataSnapshot.getValue(Course.class);
                        courseList.add(courses);
                        mCourseAdapter.setCourseData(courseList);
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    } else {
                        //Todo set no course available
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        mEmptyTextView.setText("No Course Available!");
                    }
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