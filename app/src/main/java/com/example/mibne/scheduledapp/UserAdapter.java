package com.example.mibne.scheduledapp;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder> implements Filterable {

    private Context context;
    private List<User> userList;
    private List<User> userListFiltered;
    private UserAdapterListener userAdapterListener;

    /**
     * Default Constructor for UserAdapter
     */
    public UserAdapter(Context context, List<User> userList, UserAdapterListener userAdapterListener){
        this.context = context;
        this.userAdapterListener = userAdapterListener;
        this.userList = userList;
        this.userListFiltered = userList;
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
     * @return A new UserAdapterViewHolder that holds the View for each list item
     */
    @Override
    public UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_user;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmedietly = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmedietly);
        return new UserAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the user
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param userAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(UserAdapterViewHolder userAdapterViewHolder, int position) {

        //Initialization and setting the user data into views.
        final User user = userListFiltered.get(position);

        userAdapterViewHolder.mUserNameTextView.setText(user.getName());
        userAdapterViewHolder.mUserIdTextView.setText(user.getUsername());
        userAdapterViewHolder.mUserRoleTextView.setText(user.getRole());
        userAdapterViewHolder.linearLayout.setBackgroundColor(getUserColor(user.getRole()));

        // Set an item click listener on the ListView, which sends an intent to a single User Activity
        // to know details about a notice
//        if (MainActivity.role.equals("admin")) {
//            userAdapterViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent editUserIntent = new Intent(context, EditUserActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("name", user.getName());
//                    bundle.putString("id", user.getUsername());
//                    bundle.putString("email", user.getEmail());
//                    bundle.putString("phone", user.getPhone());
//                    bundle.putString("role", user.getRole());
//                    bundle.putString("uid", user.getUid());
//                    editUserIntent.putExtras(bundle);
//                    context.startActivity(editUserIntent);
//                }
//            });
//        }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our user list
     */
    @Override
    public int getItemCount() {
        if (null == userListFiltered) return 0;
        return userListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    userListFiltered = userList;
                } else {
                    List<User>  filteredList = new ArrayList<>();
                    for (User row : userList) {
                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())
                        || row.getName().toLowerCase().contains(charString.toLowerCase())
                        || row.getPhone().toLowerCase().contains(charString.toLowerCase())
                        || row.getRole().toLowerCase().contains(charString.toLowerCase())
                        || row.getEmail().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                            Log.v("userList: ", row.getName());
                        }
                    }
                    userListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userListFiltered = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * This method is used to set the user data on a UserAdapter if we've already
     * created one. This is handy when we get new data from the firebase but don't want to create a
     * new UserAdapter to display it.
     *
     * @param userData The new weather data to be displayed.
     */
    public void setUserData(List<User> userData) {
        userList = userData;
        userListFiltered = userList;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a user list item.
     */
    public class UserAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mUserNameTextView;
        public final TextView mUserIdTextView;
        public final TextView mUserRoleTextView;
        public final LinearLayout linearLayout;

        public UserAdapterViewHolder(View itemView) {
            super(itemView);
            mUserNameTextView = (TextView) itemView.findViewById(R.id.user_name_text_view);
            mUserIdTextView = (TextView) itemView.findViewById(R.id.user_id_text_view);
            mUserRoleTextView = (TextView) itemView.findViewById(R.id.user_role_text_view);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.list_item_user);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAdapterListener.onUserSelected(userListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    /**
     * Return the day color according to the user
     * @param  day
     * @return
     */
    private int getUserColor(String day) {
        int dayColorResourceId;

        switch (day) {
            case "student" : dayColorResourceId = R.color.credit1;
                break;
            case "teacher" : dayColorResourceId = R.color.credit2;
                break;
            case "admin" : dayColorResourceId = R.color.credit4;
                break;
            default: dayColorResourceId = R.color.colorAccent;
                break;
        }
        return ContextCompat.getColor(context, dayColorResourceId);
    }

    public interface UserAdapterListener {
        void onUserSelected(User user);
    }
}
