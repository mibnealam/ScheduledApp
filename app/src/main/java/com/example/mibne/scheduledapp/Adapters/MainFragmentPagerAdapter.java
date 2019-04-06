package com.example.mibne.scheduledapp.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mibne.scheduledapp.Fragments.ThisWeekFragment;
import com.example.mibne.scheduledapp.Fragments.TodayFragment;
import com.example.mibne.scheduledapp.Fragments.TomorrowFragment;
import com.example.mibne.scheduledapp.R;

public class MainFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    public MainFragmentPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TodayFragment();
        } else if (position == 1){
            return new TomorrowFragment();
        }  else {
            return new ThisWeekFragment();
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
                return mContext.getString(R.string.category_today);
            case 1:
                return mContext.getString(R.string.category_tomorrow);
            case 2:
                return mContext.getString(R.string.category_this_week);
            default:
                return null;
        }
    }
}