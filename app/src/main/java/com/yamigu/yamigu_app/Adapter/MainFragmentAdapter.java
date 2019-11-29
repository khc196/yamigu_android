package com.yamigu.yamigu_app.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yamigu.yamigu_app.Fragment.MeetingCardFragment;

import java.util.ArrayList;

public class MainFragmentAdapter extends FragmentPagerAdapter{
    private ArrayList<Fragment> fragments = new ArrayList<>();
    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    public void addItem(Fragment fragment) {
        fragments.add(fragment);
    }

}
