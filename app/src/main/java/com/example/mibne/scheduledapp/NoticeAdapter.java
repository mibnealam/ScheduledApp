package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class NoticeAdapter extends ArrayAdapter<Notice> {
    /**
     * Default Constructor for NoticeAdapter
     * @param context
     * @param notices
     */
    public NoticeAdapter(Context context, int resource, List<Notice> notices) {
        super(context,resource, notices);
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
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item_notice, parent, false);
        }

        //TextView noticeTitleTextView = (TextView) convertView.findViewById(R.id.notice_title);
        //TextView noticeTimeTextView = (TextView) convertView.findViewById(R.id.notice_time);
        TextView noticeDescriptionTextView = (TextView) convertView.findViewById(R.id.notice_description);
        TextView noticeNoticeOwnerTextView = (TextView) convertView.findViewById(R.id.notice_heading);
        TextView noticeDeadlineTextView = (TextView) convertView.findViewById(R.id.notice_deadline);

        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.list_item_notice);

        Notice notice = getItem(position);

        Log.v("Color", notice.getNoticePriority());

        linearLayout.setBackgroundColor(getPriorityColor(notice.getNoticePriority()));

        //noticeTitleTextView.setText(notice.getNoticeTitle());

        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDateOfCreation = formatTime(notice.getNoticeDate());
        String formattedDateOfDeadline = formatDate(notice.getNoticeDate());
        // Display the creation date of the notice in that TextView
        //noticeTimeTextView.setText(formattedDateOfCreation);
        // Display the applied date of the notice in that TextView
        noticeDeadlineTextView.setText(formattedDateOfDeadline);
        noticeDescriptionTextView.setText(notice.getNoticeDescription());
        noticeNoticeOwnerTextView.setText(notice.getNoticeOwner());


        // Return the list item view that is now showing the appropriate data
        return convertView;
    }

    /**
     * Return the credit color according to the value
     * @param priority
     * @return
     */
    private int getPriorityColor(String priority) {
        int creditColorResourceId;

        if (priority == "high"){
            creditColorResourceId = R.color.priority_1;
        }else if (priority == "medium"){
            creditColorResourceId = R.color.priority_2;
        }else if (priority == "low"){
            creditColorResourceId = R.color.priority_3;
        }else {
            creditColorResourceId = R.color.priority_default;
        }

        return ContextCompat.getColor(getContext(), creditColorResourceId);
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Long dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Long dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}
