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

import com.example.mibne.scheduledapp.Activities.MainActivity;
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

    private List<Routine> routineList = new ArrayList<>();

    private ListView mRoutineListView;
    private RoutineAdapterForUser mRoutineAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;


    public TomorrowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_today);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_view_no_class);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, +1);
        String dayOfTheWeek = sdf.format(calendar.getTime());

        MainActivity activity = (MainActivity) getActivity();
        if (activity.getRoutineData() != null && !activity.getRoutineData().isEmpty()) {
            for (Routine routine: activity.getRoutineData()){
                if (routine.getDay().equals(dayOfTheWeek)){
                    routineList.add(routine);
                }
            }
            if (routineList.isEmpty()){
                mEmptyTextView.setText(R.string.prompt_no_class_tomorrow);
            }
        }

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRoutineListView = (ListView) rootView.findViewById(R.id.list_view_routine_with_category);

        mRoutineAdapter = new RoutineAdapterForUser(getContext(), routineList);

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
        mProgressBar.setVisibility(View.GONE);
        //attachDatabaseReadListener();

        return rootView;
    }
}