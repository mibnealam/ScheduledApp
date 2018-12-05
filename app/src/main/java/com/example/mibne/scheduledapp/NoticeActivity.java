package com.example.mibne.scheduledapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
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

public class NoticeActivity extends AppCompatActivity {

    public static final String LOG_TAG = NoticeActivity.class.getName();

    /**
     * Constant value for the notice loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NOTICE_LOADER_ID = 1;

    private List<Notice> noticeList = new ArrayList<>();

    private RecyclerView mNoticeRecyclerView;
    /** Adapter for the list of Notices */
    private NoticeAdapter mNoticeAdapter;
    private ProgressBar mProgressBar;
    /** TextView that is displayed when the list is empty */
    private TextView mEmptyTextView;
    private String mUsername;
    private String uid;
    private String mNoticeType;
    private String mUserOrganization;
    private String mUserDepartment;
    private String mUserBatch;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference rootRef;
    private ValueEventListener mValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        final Bundle mNoticeTypeBundle = getIntent().getExtras();
        mNoticeType = mNoticeTypeBundle.getString("Type");
        mUsername = mNoticeTypeBundle.getString("userId");
        uid = mNoticeTypeBundle.getString("uid");
        mUserOrganization = mNoticeTypeBundle.getString("organization");
        mUserDepartment = mNoticeTypeBundle.getString("department");
        String role = mNoticeTypeBundle.getString("role");
        mUserBatch = mUsername.substring(5,7);
        getSupportActionBar().setTitle(mNoticeType);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton fabNotice = findViewById(R.id.fab_notice);

        if (role.equals("admin") || role.equals("teacher")) {
            fabNotice.setVisibility(View.VISIBLE);
        } else {
            fabNotice.setVisibility(View.GONE);
        }

        fabNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateNoticeActivity.class);
                intent.putExtras(mNoticeTypeBundle);
                startActivity(intent);
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //Todo : put feature user preferences to get data from different nodes and show
        rootRef = mFirebaseDatabase.getReference();

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view_notice);
        mNoticeRecyclerView = (RecyclerView) findViewById(R.id.notice_recycler_view);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mNoticeRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNoticeRecyclerView.setHasFixedSize(true);

        /*
         * The CourseAdapter is responsible for linking our course data with the Views that
         * will end up displaying our course data.
         */
        mNoticeAdapter = new NoticeAdapter();

        mNoticeRecyclerView.setAdapter(mNoticeAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            //loaderManager.initLoader(NOTICE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyTextView.setText(R.string.prompt_no_internet_connection);
        }
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        noticeList.clear();
        attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot noticeSnapshot: dataSnapshot.getChildren()) {
                        Notice notice =  noticeSnapshot.getValue(Notice.class);
                        noticeList.add(notice);
                    }
                    mNoticeAdapter.setNoticeData(noticeList);
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    switch (mNoticeType) {
                        case "Assignment" :
                            mEmptyTextView.setText(R.string.prompt_no_assignment);
                            break;
                        case "Notice" :
                            mEmptyTextView.setText(R.string.prompt_no_notice);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.child(mUserOrganization).child("notices").orderByChild("noticeType").equalTo(mNoticeType).addValueEventListener(mValueEventListener);
        rootRef.child(mUserOrganization).child(mUserDepartment).child("notices").orderByChild("noticeType").equalTo(mNoticeType).addValueEventListener(mValueEventListener);
        //Todo fetch notices from sub/cse/courses/courseCode/notices if uid exists
        //rootRef.child("sub").child("cse").child("courses").child("notices").orderByChild(uid).equalTo(uid).addValueEventListener(mValueEventListener);
        rootRef.child(mUserOrganization).child(mUserDepartment).child(mUserBatch).child("notices").orderByChild("noticeType").equalTo(mNoticeType).addValueEventListener(mValueEventListener);
        rootRef.child("users").child(uid).child("notices").orderByChild("noticeType").equalTo(mNoticeType).addValueEventListener(mValueEventListener);
    }
    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            rootRef.removeEventListener(mValueEventListener);
            noticeList.clear();
            mValueEventListener = null;
        }
    }
}