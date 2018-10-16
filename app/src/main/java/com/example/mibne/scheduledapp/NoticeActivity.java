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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class NoticeActivity extends AppCompatActivity {
    private ListView mNoticeListView;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private String mUsername;
    private String uid;
    private String mNoticeType;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference rootRef;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;

    /**
     * Constant value for the notice loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NOTICE_LOADER_ID = 1;

    /** Adapter for the list of Notices */
    private NoticeAdapter mNoticeAdapter;


    public static final String LOG_TAG = NoticeActivity.class.getName();

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uid = user.getUid();
        } else {
            // No user is signed in
        }

        Bundle mNoticeTypeBundle = getIntent().getExtras();
        mNoticeType = mNoticeTypeBundle.getString("Type");
        getSupportActionBar().setTitle(mNoticeType);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton fabNotice = findViewById(R.id.fab_notice);
        fabNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateNoticeActivity.class);
                intent.putExtra("Type", mNoticeType);
                startActivity(intent);
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //Todo : put feature user preferences to get data from different nodes and show
        rootRef = mFirebaseDatabase.getReference();

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view_notice);
        mNoticeListView = (ListView) findViewById(R.id.notice_list_view);

        // Initialize notice ListView and its adapter
        final List<Notice> notices = new ArrayList<>();
        mNoticeAdapter = new NoticeAdapter(this, R.layout.list_item_notice, notices);
        mNoticeListView.setAdapter(mNoticeAdapter);

        // Set an item click listener on the ListView, which sends an intent to a single Notice Activity
        // to know details about a notice
        mNoticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current notice that was clicked on
                Notice currentNotice = mNoticeAdapter.getItem(position);

                Intent noticeFullViewIntent = new Intent(getApplicationContext(), SingleNoticeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Type", currentNotice.getNoticeType());
                bundle.putString("Title", currentNotice.getNoticeTitle());
                bundle.putString("Date", currentNotice.getNoticeDate().toString());
                bundle.putString("Description", currentNotice.getNoticeDescription());
                bundle.putString("Owner", currentNotice.getNoticeOwner());
                bundle.putString("Deadline", currentNotice.getNoticeDeadline().toString());
                noticeFullViewIntent.putExtras(bundle);
                startActivity(noticeFullViewIntent);
            }
        });

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
            mEmptyStateTextView.setText(R.string.prompt_no_internet_connection);
        }
        View view = findViewById(R.id.progress_bar);
        view.setVisibility(View.GONE);
        attachDatabaseReadListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        mNoticeAdapter.clear();
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
                    mProgressBar.setVisibility(INVISIBLE);
                } else {
                    mProgressBar.setVisibility(INVISIBLE);
                    mEmptyTextView.setText(R.string.prompt_no_notice);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //rootRef.child("sub").addListenerForSingleValueEvent(mValueEventListener);

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
            rootRef.child("sub").child("notices").orderByChild("noticeType").equalTo(mNoticeType).addChildEventListener(mChildEventListener);
            rootRef.child("sub").child("cse").child("notices").orderByChild("noticeType").equalTo(mNoticeType).addChildEventListener(mChildEventListener);
            //Todo fetch notices from sub/cse/courses/courseCode/notices if uid exists
            //rootRef.child("sub").child("cse").child("courses").child("notices").orderByChild(uid).equalTo(uid).addChildEventListener(mChildEventListener);
            rootRef.child("sub").child("cse").child("37").child("notices").orderByChild("noticeType").equalTo(mNoticeType).addChildEventListener(mChildEventListener);
            rootRef.child("users").child(uid).child("notices").orderByChild("noticeType").equalTo(mNoticeType).addChildEventListener(mChildEventListener);
        }
    }
    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            rootRef.removeEventListener(mChildEventListener);
            mNoticeAdapter.clear();
            mChildEventListener = null;
        }
    }
}