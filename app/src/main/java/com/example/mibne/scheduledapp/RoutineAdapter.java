package com.example.mibne.scheduledapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.mibne.scheduledapp.MainActivity.role;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineAdapterViewHolder> implements Filterable {

    private Context context;

    private List<Routine> routineList;
    private List<Routine> routineListFiltered;
    private RoutineAdapterListener routineAdapterListener;

    /**
     * Default Constructor for RoutineAdapter
     */

    public RoutineAdapter(Context context, List<Routine> routineList, RoutineAdapterListener routineAdapterListener){
        this.context = context;
        this.routineAdapterListener = routineAdapterListener;
        this.routineList = routineList;
        this.routineListFiltered = routineList;
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
     * @return A new RoutineAdapterViewHolder that holds the View for each list item
     */
    @Override
    public RoutineAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_full_routine;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmedietly = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmedietly);
        return new RoutineAdapter.RoutineAdapterViewHolder(view);
    }



    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the routine
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param routineAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RoutineAdapterViewHolder routineAdapterViewHolder, final int position) {

        //Initialization and setting the routine data into views.
        final Routine routine = routineListFiltered.get(position);

        routineAdapterViewHolder.mRoutineDayTextView.setText(routine.getDay().substring(0, 3).toUpperCase());
        routineAdapterViewHolder.mRoutineCourseCodeTextView.setText(routine.getCourseCode());
        routineAdapterViewHolder.mRoutineTimeTextView.setText(routine.getStartTime() + "-" + routine.getEndTime());
        routineAdapterViewHolder.mRoutineRoomNoTextView.setText(routine.getRoomNo());
        routineAdapterViewHolder.linearLayout.setBackgroundColor(getDayColor(routine.getDay().toLowerCase()));

        // Set an item click listener on the ListView, which sends an intent to a single Routine Activity
        // to know details about a notice
//        if (role.equals("admin")) {
//            routineAdapterViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent editRoutineIntent = new Intent(context, EditRoutineActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("courseCode", routine.getCourseCode());
//                    bundle.putString("day", routine.getDay());
//                    bundle.putString("startTime", routine.getStartTime());
//                    bundle.putString("endTime", routine.getEndTime());
//                    bundle.putString("roomNo", routine.getRoomNo());
//                    editRoutineIntent.putExtras(bundle);
//                    context.startActivity(editRoutineIntent);
//                }
//            });
//        }
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our routinelist
     */
    @Override
    public int getItemCount() {
        if ( null == routineListFiltered) return 0;
        return routineListFiltered.size();
    }

    /**
     * This method is used to set the routine data on a RoutineAdapter if we've already
     * created one. This is handy when we get new data from the firebase but don't want to create a
     * new RoutineAdapter to display it.
     *
     * @param routineData The new weather data to be displayed.
     */
    public void setRoutineData(List<Routine> routineData) {
        routineList = routineData;
        routineListFiltered = routineList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    routineListFiltered = routineList;
                } else {
                    List<Routine>  filteredList = new ArrayList<>();
                    for (Routine row : routineList) {
                        if (row.getCourseCode().toLowerCase().contains(charString.toLowerCase())
                                || row.getDay().toLowerCase().contains(charString.toLowerCase())
                                || row.getRoomNo().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    routineListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = routineListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                routineListFiltered = (ArrayList<Routine>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    /**
     * Cache of the children views for a routine list item.
     */
    public class RoutineAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mRoutineCourseCodeTextView;
        public final TextView mRoutineRoomNoTextView;
        public final TextView mRoutineDayTextView;
        public final TextView mRoutineTimeTextView;
        public final LinearLayout linearLayout;

        public RoutineAdapterViewHolder(View itemView) {
            super(itemView);
            mRoutineCourseCodeTextView = (TextView) itemView.findViewById(R.id.routine_course_code);
            mRoutineDayTextView = (TextView) itemView.findViewById(R.id.routine_day);
            mRoutineTimeTextView = (TextView) itemView.findViewById(R.id.routine_time);
            mRoutineRoomNoTextView = (TextView) itemView.findViewById(R.id.routine_room_no);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.list_item_routine);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    routineAdapterListener.onRoutineSelected(routineListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    /**
     * Return the day color according to the day
     * @param  day
     * @return
     */
    private int getDayColor(String day) {
        int dayColorResourceId;

        switch (day) {
            case "saturday" : dayColorResourceId = R.color.credit1;
                break;
            case "sunday" : dayColorResourceId = R.color.credit2;
                break;
            case "monday" : dayColorResourceId = R.color.credit3;
                break;
            case "tuesday" : dayColorResourceId = R.color.credit4;
                break;
            case "wednesday" : dayColorResourceId = R.color.credit5;
                break;
            case "thursday" : dayColorResourceId = R.color.credit6;
                break;
            default: dayColorResourceId = R.color.colorAccent;
                break;
        }
        return ContextCompat.getColor(context, dayColorResourceId);
    }

    public interface RoutineAdapterListener {
        void onRoutineSelected(Routine routine);
    }
}