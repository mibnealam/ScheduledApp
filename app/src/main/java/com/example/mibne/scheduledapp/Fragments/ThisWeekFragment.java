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


    private List<Routine> routineList = new ArrayList<>();

    private RecyclerView mRoutineRecyclerView;
    private RoutineAdapter mRoutineAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private String role;

    public ThisWeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_this_week, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_weekly_user_routine_list);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_view_weekly_user_routine_list);

        MainActivity activity = (MainActivity) getActivity();
        if (activity.getRoutineData() != null && !activity.getRoutineData().isEmpty()) {
            routineList = sortRoutineByDay(activity.getRoutineData());
        } else {
            mEmptyTextView.setText(R.string.prompt_no_class_this_week);
        }

        role = userDataBundle.getString("role");

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
        mProgressBar.setVisibility(ProgressBar.GONE);

        return rootView;
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