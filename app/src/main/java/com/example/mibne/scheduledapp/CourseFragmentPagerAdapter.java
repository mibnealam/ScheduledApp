package com.example.mibne.scheduledapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class CourseFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private Context mContext;

    public CourseFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FixedCoursesFragment();
        } else if (position == 1){
            return new AllCoursesFragment();
        }  else {
            return new EnrolledCoursesFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.category_fixed_courses);
            case 1:
                return mContext.getString(R.string.category_all_courses);
            case 2:
                return mContext.getString(R.string.category_enrolled_courses);
            default:
                return null;
        }
    }
}
