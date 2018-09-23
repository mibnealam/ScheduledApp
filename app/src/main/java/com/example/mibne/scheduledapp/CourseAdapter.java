package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<Course> {
    /**
     * Default Constructor for CourseAdapter
     * @param context
     * @param courses
     */
    public CourseAdapter(Context context, int resource, List<Course> courses) {
        super(context,resource, courses);
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
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item_course, parent, false);
        }

        TextView creditTextView = (TextView) convertView.findViewById(R.id.course_credit);
        GradientDrawable creditCircle = (GradientDrawable) creditTextView.getBackground();
        TextView courseCodeTextView = (TextView) convertView.findViewById(R.id.course_code);
        TextView courseNameTextView = (TextView) convertView.findViewById(R.id.course_name);

        Course course = getItem(position);

        creditTextView.setText(course.getCourseCredit());
        creditCircle.setColor(getCreditColor(course.getCourseCredit()));
        courseCodeTextView.setText(course.getCourseCode());
        courseNameTextView.setText(course.getCourseName());

        // Return the list item view that is now showing the appropriate data
        return convertView;
    }

    /**
     * Return the credit color according to the value
     * @param credit
     * @return
     */
    private int getCreditColor(String credit) {
        int creditColorResourceId;

        switch (credit) {
            case "0.75" : creditColorResourceId = R.color.credit1;
                break;
            case "1.5" : creditColorResourceId = R.color.credit2;
                break;
            case "2.0" : creditColorResourceId = R.color.credit3;
                break;
            case "3.0" : creditColorResourceId = R.color.credit4;
                break;
            case "3.5" : creditColorResourceId = R.color.credit5;
                break;
            default: creditColorResourceId = R.color.colorAccent;
                break;
        }

        return ContextCompat.getColor(getContext(), creditColorResourceId);
    }
}
