package com.example.mibne.scheduledapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mibne.scheduledapp.Activities.SingleNoticeActivity;
import com.example.mibne.scheduledapp.Models.Notice;
import com.example.mibne.scheduledapp.R;

import java.text.SimpleDateFormat;
import java.util.List;


public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeAdapterViewHolder> {

    private List<Notice> noticeList;

    private Context context;
    /**
     * Default Constructor for NoticeAdapter
     */
    public NoticeAdapter() {
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NoticeAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public NoticeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_notice;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmedietly = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmedietly);
        return new NoticeAdapter.NoticeAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the notice
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param noticeAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(NoticeAdapterViewHolder noticeAdapterViewHolder, int position) {

        //Initialization and setting the notice data into views.
        final Notice notice = noticeList.get(position);

        noticeAdapterViewHolder.mNoticeNoticeOwnerTextView.setText(notice.getNoticeOwner());
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDateOfDeadline = formatDate(notice.getNoticeDate());
        noticeAdapterViewHolder.mNoticeDeadlineTextView.setText(formattedDateOfDeadline);
        noticeAdapterViewHolder.mNoticeDescriptionTextView.setText(notice.getNoticeDescription());
        noticeAdapterViewHolder.linearLayout.setBackgroundColor(getPriorityColor(notice.getNoticePriority()));


        // Set an item click listener on the ListView, which sends an intent to a single Notice Activity
        // to know details about a notice
        noticeAdapterViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noticeFullViewIntent = new Intent(context, SingleNoticeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Type", notice.getNoticeType());
                bundle.putString("Title", notice.getNoticeTitle());
                bundle.putString("Date", notice.getNoticeDate().toString());
                bundle.putString("Description", notice.getNoticeDescription());
                bundle.putString("Owner", notice.getNoticeOwner());
                bundle.putString("Deadline", notice.getNoticeDeadline().toString());
                noticeFullViewIntent.putExtras(bundle);
                context.startActivity(noticeFullViewIntent);
            }
        });
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our notice list
     */
    @Override
    public int getItemCount() {
        if ( null == noticeList) return 0;
        return noticeList.size();
    }

    /**
     * This method is used to set the notice data on a NoticeAdapter if we've already
     * created one. This is handy when we get new data from the firebase but don't want to create a
     * new NoticeAdapter to display it.
     *
     * @param noticeData The new weather data to be displayed.
     */
    public void setNoticeData(List<Notice> noticeData) {
        noticeList = noticeData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a notice list item.
     */
    public class NoticeAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mNoticeNoticeOwnerTextView;
        public final TextView mNoticeDeadlineTextView;
        public final TextView mNoticeDescriptionTextView;
        public final LinearLayout linearLayout;

        public NoticeAdapterViewHolder(View itemView) {
            super(itemView);
            mNoticeNoticeOwnerTextView = (TextView) itemView.findViewById(R.id.notice_heading);
            mNoticeDeadlineTextView = (TextView) itemView.findViewById(R.id.notice_deadline);
            mNoticeDescriptionTextView = (TextView) itemView.findViewById(R.id.notice_description);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.list_item_notice);
        }
    }

    /**
     * Return the priority color according to the value
     * @param priority
     * @return
     */
    private int getPriorityColor(String priority) {
        int priorityColorResourceId;
        switch (priority.toLowerCase()) {
            case "high" : priorityColorResourceId = R.color.priority_1;
                break;
            case "medium" : priorityColorResourceId = R.color.priority_2;
                break;
            case "low" : priorityColorResourceId = R.color.priority_3;
                break;
            default: priorityColorResourceId = R.color.priority_default;
                break;
        }

        return ContextCompat.getColor(context, priorityColorResourceId);
    }

    /**
     * Return the formatted date string (i.e. "Mar 3") from a Date object.
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
