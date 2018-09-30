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
 * {@link TomorrowFragment} interface
 * to handle interaction events.
 * Use the {@link TomorrowFragment} factory method to
 * create an instance of this fragment.
 */

public class TomorrowFragment extends Fragment {


    public TomorrowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tomorrow, container, false);

        return rootView;
    }
}