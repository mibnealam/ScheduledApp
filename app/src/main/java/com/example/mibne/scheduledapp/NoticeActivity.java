package com.example.mibne.scheduledapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class NoticeActivity extends AppCompatActivity {
    private ListView mNoticeListView;
    private NoticeAdapter mNoticeAdapter;
    private ProgressBar mProgressBar;
    private String mUsername;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoticeDatabaseReferance;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mNoticeDatabaseReferance = mFirebaseDatabase.getReference().child("notices");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mNoticeListView = (ListView) findViewById(R.id.notice_list_view);

        // Initialize notice ListView and its adapter
        List<Notice> notices = new ArrayList<>();
        mNoticeAdapter = new NoticeAdapter(this, R.layout.list_item_notice, notices);
        mNoticeListView.setAdapter(mNoticeAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        attachDatabaseReadListener();
    }


    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Notice notice = dataSnapshot.getValue(Notice.class);
                    mNoticeAdapter.add(notice);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mNoticeDatabaseReferance.addChildEventListener(mChildEventListener);
        }
    }
    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mNoticeDatabaseReferance.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}