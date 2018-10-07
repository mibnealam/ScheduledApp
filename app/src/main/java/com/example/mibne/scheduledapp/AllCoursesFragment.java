package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.INVISIBLE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayFragment} interface
 * to handle interaction events.
 * Use the {@link AllCoursesFragment#} factory method to
 * create an instance of this fragment.
 */
public class AllCoursesFragment extends Fragment {
    private static final int READ_REQUEST_CODE = 42;

    private List<Course> courseList = new ArrayList<>();

    private RecyclerView mCourseRecyclerView;
    private CourseAdapter mCourseAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private String mUserDepartment;
    private String mUserOrganization;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCourseDatabaseReferance;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;


    public AllCoursesFragment() {
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
                performFileSearch();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mUserOrganization = "sub";
        mUserDepartment = "cse";
        mCourseDatabaseReferance = mFirebaseDatabase.getReference().child(mUserOrganization + "/" + mUserDepartment + "/courses");
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
                Log.i("uri_of_data", "Uri: " + uri.toString());
                //showImage(uri);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        courseList.clear();
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
                    Course courses =  dataSnapshot.getValue(Course.class);
                    courseList.add(courses);
                    mCourseAdapter.setCourseData(courseList);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
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
}