package com.example.mibne.scheduledapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mibne.scheduledapp.Models.Course;
import com.example.mibne.scheduledapp.Models.Notice;
import com.example.mibne.scheduledapp.Adapters.NoticeAdapter;
import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.Routine;
import com.example.mibne.scheduledapp.Adapters.RoutineAdapterForUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.mibne.scheduledapp.Activities.MainActivity.userDataBundle;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TomorrowFragment} interface
 * to handle interaction events.
 * Use the {@link TomorrowFragment} factory method to
 * create an instance of this fragment.
 */

public class TomorrowFragment extends Fragment {

    private String TAG = "TodayFragment";

    private String uid;

    private List<Notice> noticeList = new ArrayList<>();

    private List<String> courseList = new ArrayList<>();

    private RecyclerView mNoticeRecyclerView;
    private ListView mRoutineListView;
    private NoticeAdapter mNoticeAdapter;
    private RoutineAdapterForUser mRoutineAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private String mUserDepartment;
    private String mUserOrganization;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRoutineDatabaseReferance;
    private DatabaseReference mUserDatabaseReferance;
    private ValueEventListener mValueEventListenerForRoutine;
    private ValueEventListener mValueEventListenerForUser;


    public TomorrowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uid = user.getUid();
        } else {
            // No user is signed in
        }


        mUserOrganization = userDataBundle.getString("organization");
        mUserDepartment = userDataBundle.getString("department");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRoutineDatabaseReferance = mFirebaseDatabase.getReference().child(mUserOrganization + "/" + mUserDepartment + "/routines");
        mUserDatabaseReferance = mFirebaseDatabase.getReference().child("users/" + uid + "/courses");
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_today);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_view_no_class);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRoutineListView = (ListView) rootView.findViewById(R.id.list_view_routine_with_category);

        mRoutineAdapter = new RoutineAdapterForUser(getContext(), new ArrayList<Routine>());

        mRoutineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "item" + position + " just clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRoutineListView.setAdapter(mRoutineAdapter);


        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        attachDatabaseReadListener();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        noticeList.clear();
        courseList.clear();
        //attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        noticeList.clear();
        courseList.clear();
    }

    private void attachDatabaseReadListener() {
        mValueEventListenerForRoutine = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot routineDataSnapshot: dataSnapshot.getChildren()) {
                        Routine routine =  routineDataSnapshot.getValue(Routine.class);
                        mRoutineAdapter.add(routine);
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setText(R.string.prompt_no_class_tomorrow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, +1);
        final String dayOfTheWeek = sdf.format(calendar.getTime());
        Log.v(TAG, dayOfTheWeek);

        mValueEventListenerForUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot routineDataSnapshot: dataSnapshot.getChildren()) {
                        Course course =  routineDataSnapshot.getValue(Course.class);
                        mRoutineDatabaseReferance.orderByKey().equalTo(dayOfTheWeek + "-" + course.getCourseCode()).addValueEventListener(mValueEventListenerForRoutine);
                    }

                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setText(R.string.prompt_no_class_tomorrow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUserDatabaseReferance.addValueEventListener(mValueEventListenerForUser);

    }
    private void detachDatabaseReadListener() {
        if (mValueEventListenerForRoutine != null && mValueEventListenerForUser != null) {
            mRoutineDatabaseReferance.removeEventListener(mValueEventListenerForRoutine);
            mUserDatabaseReferance.removeEventListener(mValueEventListenerForUser);
            mValueEventListenerForUser = null;
            mValueEventListenerForRoutine = null;
        }
    }
}