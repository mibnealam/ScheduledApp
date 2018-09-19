package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class RoutineAdapter extends ArrayAdapter<Routine> {
    /**
     * Default Constructor for NoticeAdapter
     *
     * @param context
     * @param routines
     */
    public RoutineAdapter(Context context, int resource, List<Routine> routines) {
        super(context, resource, routines);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item_routine, parent, false);
        }

        TextView classTimeTextView = (TextView) convertView.findViewById(R.id.class_time);
        TextView classRoomTextView = (TextView) convertView.findViewById(R.id.class_room);
        TextView courseCodeTextView = (TextView) convertView.findViewById(R.id.course_code);

        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.list_item_notice);

        Routine routine = getItem(position);

        classTimeTextView.setText(routine.getStartTime().toString() + "-" + routine.getEndTime().toString());
        classRoomTextView.setText(routine.getRoomNo());
        courseCodeTextView.setText(routine.getCourseCode());


        // Return the list item view that is now showing the appropriate data
        return convertView;
    }
}