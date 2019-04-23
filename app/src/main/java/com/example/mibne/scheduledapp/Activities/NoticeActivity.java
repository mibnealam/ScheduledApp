package com.example.mibne.scheduledapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mibne.scheduledapp.Models.Notice;
import com.example.mibne.scheduledapp.Adapters.NoticeAdapter;
import com.example.mibne.scheduledapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.mibne.scheduledapp.Activities.LoginActivity.checkConnection;

public class NoticeActivity extends AppCompatActivity {

    public static final String TAG = NoticeActivity.class.getName();

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

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        findViewById(R.id.no_internet).setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        //final Bundle mNoticeTypeBundle = getIntent().getExtras();
        mNoticeType = "Notice";
        mUsername = sharedPreferences.getString("userId", null);
        uid = sharedPreferences.getString("uid", null);
        mUserOrganization = sharedPreferences.getString("organization", null);
        mUserDepartment = sharedPreferences.getString("department", null);
        String role = sharedPreferences.getString("role", "student");
        if (mUsername.length() == 14) {
            mUserBatch = mUsername.substring(5,7);
        } else {
            mUserBatch = "00";
        }
        getSupportActionBar().setTitle(mNoticeType);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton fabNotice = findViewById(R.id.fab_notice);

        if (role.equals("admin") || role.equals("super")) {
            fabNotice.setVisibility(View.VISIBLE);
        } else {
            fabNotice.setVisibility(View.GONE);
        }

        fabNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateNoticeActivity.class);
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
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
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

        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        noticeList.clear();
        if (checkConnection(NoticeActivity.this)) {
            attachDatabaseReadListener();
        } else {
            mProgressBar.setVisibility(View.GONE);
            findViewById(R.id.no_internet).setVisibility(View.VISIBLE);
        }
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
                    findViewById(R.id.no_internet).setVisibility(View.GONE);
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
        rootRef.child(mUserOrganization).child("notices").orderByChild("noticeType").equalTo(mNoticeType).limitToLast(100).addValueEventListener(mValueEventListener);
        rootRef.child(mUserOrganization).child(mUserDepartment).child("notices").orderByChild("noticeType").equalTo(mNoticeType).limitToLast(400).addValueEventListener(mValueEventListener);
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