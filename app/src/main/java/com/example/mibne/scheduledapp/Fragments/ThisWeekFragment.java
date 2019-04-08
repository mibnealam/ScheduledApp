package com.example.mibne.scheduledapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mibne.scheduledapp.Activities.EditRoutineActivity;
import com.example.mibne.scheduledapp.Activities.MainActivity;
import com.example.mibne.scheduledapp.Models.Course;
import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.Routine;
import com.example.mibne.scheduledapp.Adapters.RoutineAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static com.example.mibne.scheduledapp.Activities.MainActivity.userDataBundle;
import static com.example.mibne.scheduledapp.Activities.RoutineActivity.sortRoutineByDay;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThisWeekFragment} interface
 * to handle interaction events.
 * Use the {@link ThisWeekFragment} factory method to
 * create an instance of this fragment.
 */

public class ThisWeekFragment extends Fragment implements RoutineAdapter.RoutineAdapterListener {

    private String TAG = "ThisWeekFragment";

    private String uid;

    private List<Routine> routineList = new ArrayList<>();

    private RecyclerView mRoutineRecyclerView;
    private RoutineAdapter mRoutineAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private String mUserDepartment;
    private String mUserOrganization;
    private String role;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRoutineDatabaseReferance;
    private DatabaseReference mUserDatabaseReference;
    private ValueEventListener mValueEventListenerForRoutine;
    private ValueEventListener mValueEventListenerForUser;

    public ThisWeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_this_week, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uid = user.getUid();
        } else {
            // No user is signed in
        }

        MainActivity activity = (MainActivity) getActivity();
        List<Routine> routineList = activity.getRoutineData();

        mUserOrganization = userDataBundle.getString("organization");
        mUserDepartment = userDataBundle.getString("department");
        role = userDataBundle.getString("role");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRoutineDatabaseReferance = mFirebaseDatabase.getReference().child(mUserOrganization + "/" + mUserDepartment + "/routines");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users/" + uid + "/courses");

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_weekly_user_routine_list);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_view_weekly_user_routine_list);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRoutineRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_weekly_user_routine);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRoutineRecyclerView.setLayoutManager(layoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRoutineRecyclerView.setHasFixedSize(true);
        /*
         * The CourseAdapter is responsible for linking our course data with the Views that
         * will end up displaying our course data.
         */
        mRoutineAdapter = new RoutineAdapter(getContext(), routineList, this);

        mRoutineRecyclerView.setAdapter(mRoutineAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        routineList.clear();
        attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        routineList.clear();
    }


    private void attachDatabaseReadListener() {
        mValueEventListenerForRoutine = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot routineDataSnapshot: dataSnapshot.getChildren()) {
                        Routine routine =  routineDataSnapshot.getValue(Routine.class);
                        routineList.add(routine);
                    }
                    mRoutineAdapter.setRoutineData(sortRoutineByDay(routineList));
                    mEmptyTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(INVISIBLE);
                } else {
                    mProgressBar.setVisibility(INVISIBLE);
                    mEmptyTextView.setText(R.string.prompt_no_routine);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mValueEventListenerForUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot routineDataSnapshot: dataSnapshot.getChildren()) {
                        Course course =  routineDataSnapshot.getValue(Course.class);
                        mRoutineDatabaseReferance.orderByChild("courseCode").equalTo(course.getCourseCode()).addValueEventListener(mValueEventListenerForRoutine);
                    }

                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setText(R.string.prompt_no_class_this_week);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUserDatabaseReference.addValueEventListener(mValueEventListenerForUser);
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListenerForRoutine != null) {
            mRoutineDatabaseReferance.removeEventListener(mValueEventListenerForRoutine);
            mUserDatabaseReference.removeEventListener(mValueEventListenerForUser);
            mValueEventListenerForRoutine = null;
        }
    }

    @Override
    public void onRoutineSelected(Routine routine) {
        if (role.equals("admin")) {
            Intent editRoutineIntent = new Intent(getContext(), EditRoutineActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("courseCode", routine.getCourseCode());
            bundle.putString("day", routine.getDay());
            bundle.putString("startTime", routine.getStartTime());
            bundle.putString("endTime", routine.getEndTime());
            bundle.putString("roomNo", routine.getRoomNo());
            editRoutineIntent.putExtras(bundle);
            this.startActivity(editRoutineIntent);
        }
    }
}