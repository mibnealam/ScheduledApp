package com.example.mibne.scheduledapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThisWeekFragment} interface
 * to handle interaction events.
 * Use the {@link ThisWeekFragment} factory method to
 * create an instance of this fragment.
 */

public class ThisWeekFragment extends Fragment {


    public ThisWeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_this_week, container, false);

        return rootView;
    }
}