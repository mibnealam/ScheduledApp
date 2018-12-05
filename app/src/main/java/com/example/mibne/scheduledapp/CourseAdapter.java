package com.example.mibne.scheduledapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseAdapterViewHolder> {

    //firebase database reference
    private DatabaseReference mDatabase;
    private DatabaseReference courseDatabaseRef;

    private String uid;
    private Context context;
    private List<Course> courseList;
    /**
     * Default Constructor for CourseAdapter
     */
    public CourseAdapter() {
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new CourseAdapterViewHolder that holds the View for each list item
     */
    @Override
    public CourseAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_course;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CourseAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the course
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param courseAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(final CourseAdapterViewHolder courseAdapterViewHolder, int position) {

        //Gets the current uid and initializes into variable uid
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uid = user.getUid();
        } else {
            // No user is signed in
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users/" + uid);
        courseDatabaseRef = FirebaseDatabase.getInstance().getReference("sub/cse/courses");

        //Initialization and setting the course data into views.
        final Course course = courseList.get(position);
        courseAdapterViewHolder.creditCircle.setColor(getCreditColor(course.getCourseCredit()));
        courseAdapterViewHolder.mCourseCreditTextView.setText(course.getCourseCredit());
        courseAdapterViewHolder.mCourseCodeTextView.setText(course.getCourseCode());
        courseAdapterViewHolder.mCourseNameTextView.setText(course.getCourseName());

        //in some cases, it will prevent unwanted situations
        courseAdapterViewHolder.checkBox.setOnCheckedChangeListener(null);

        //if true, checkbox will be selected, else unselected
        courseAdapterViewHolder.checkBox.setChecked(courseList.get(position).isSelected());

        courseAdapterViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                courseList.get(courseAdapterViewHolder.getAdapterPosition()).setSelected(isChecked);
                if (isChecked) {
                    //Add a course into users/uid/courses object of firebase database when a course is checked
                    mDatabase.child("courses").child(course.getCourseCode()).setValue(courseList.get(courseAdapterViewHolder.getAdapterPosition()));
                    courseDatabaseRef.child(course.getCourseCode()).child(uid).setValue("true");
                    //Todo: also add this user id into the selected course fixed course
                } else {
                    //Delete a course from users/uid/courses object of firebase database when a course is unchecked
                    mDatabase.child("courses").child(course.getCourseCode()).setValue(null);
                    courseDatabaseRef.child(course.getCourseCode()).child(uid).setValue(null);
                    //Todo: also remove this user id from the selected course course fixed course
                }
            }
        });
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our course list
     */

    @Override
    public int getItemCount() {
        if ( null == courseList) return 0;
        return courseList.size();
    }

    /**
     * This method is used to set the course data on a CourseAdapter if we've already
     * created one. This is handy when we get new data from the firebase but don't want to create a
     * new CourseAdapter to display it.
     *
     * @param courseData The new course data to be displayed.
     */
    public void setCourseData(List<Course> courseData) {
        courseList = courseData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a course list item.
     */
    public class CourseAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mCourseCodeTextView;
        public final TextView mCourseNameTextView;
        public final TextView mCourseCreditTextView;
        public final GradientDrawable creditCircle;
        public final CheckBox checkBox;

        public CourseAdapterViewHolder(View itemView) {
            super(itemView);
            mCourseCodeTextView = (TextView) itemView.findViewById(R.id.course_code);
            mCourseNameTextView = (TextView) itemView.findViewById(R.id.course_name);
            mCourseCreditTextView = (TextView) itemView.findViewById(R.id.course_credit);
            creditCircle = (GradientDrawable) mCourseCreditTextView.getBackground();
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_subject);
        }
    }

    /**
     * Return the credit color according to the value
     * @param //credit
     * @return
     */
    private int getCreditColor(String credit) {
        int creditColorResourceId;

        switch (credit) {
            case "0.75" : creditColorResourceId = R.color.credit1;
                break;
            case "1.0" : creditColorResourceId = R.color.credit2;
                break;
            case "1.5" : creditColorResourceId = R.color.credit3;
                break;
            case "2.0" : creditColorResourceId = R.color.credit4;
                break;
            case "3.0" : creditColorResourceId = R.color.credit5;
                break;
            case "4.0" : creditColorResourceId = R.color.credit6;
                break;
            default: creditColorResourceId = R.color.colorAccent;
                break;
        }
        return ContextCompat.getColor(context, creditColorResourceId);
    }
}
