package com.example.mibne.scheduledapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RoutineAdapterForUser extends ArrayAdapter<Routine> {

    /**
     * Default Constructor for RoutineAdapterForUser
     * @param context
     * @param routines
     */
    public RoutineAdapterForUser(Context context, List<Routine> routines) {
        super(context,0, routines);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_routine, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        Routine currentRoutine = getItem(position);

        TextView mRoutineCourseCodeTextView = (TextView) listItemView.findViewById(R.id.course_code_routine);
        TextView mRoutineTimeTextView = (TextView) listItemView.findViewById(R.id.class_time);
        TextView mRoutineRoomNoTextView = (TextView) listItemView.findViewById(R.id.class_room);
        LinearLayout linearLayout = (LinearLayout) listItemView.findViewById(R.id.list_item_user_routine);


        mRoutineCourseCodeTextView.setText(currentRoutine.getCourseCode());
        mRoutineTimeTextView.setText(currentRoutine.getStartTime().replaceAll("[A-Z ]", "") + "-" + currentRoutine.getEndTime().replaceAll("[A-Z ]", ""));
        mRoutineRoomNoTextView.setText(currentRoutine.getRoomNo());
        linearLayout.setBackgroundColor(getRoutineColor(currentRoutine.getStartTime().replaceAll("[A-Z ]", "")));

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the day color according to the time
     * @param  time
     * @return
     */
    private int getRoutineColor(String time) {
        int dayColorResourceId;

        switch (time) {
            case "08.00" : dayColorResourceId = R.color.credit1;
                break;
            case "09.30" : dayColorResourceId = R.color.credit2;
                break;
            case "11.00" : dayColorResourceId = R.color.credit3;
                break;
            case "12.30" : dayColorResourceId = R.color.credit4;
                break;
            case "02.00" : dayColorResourceId = R.color.credit5;
                break;
            case "03.30" : dayColorResourceId = R.color.credit6;
                break;
            default: dayColorResourceId = R.color.blue_gray;
                break;
        }
        return ContextCompat.getColor(getContext(), dayColorResourceId);
    }
}
