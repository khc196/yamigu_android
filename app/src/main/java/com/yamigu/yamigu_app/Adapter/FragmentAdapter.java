package com.yamigu.yamigu_app.Adapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yamigu.yamigu_app.Fragment.MeetingCardFragment;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

    // ViewPager에 들어갈 Fragment들을 담을 리스트
    private ArrayList<MeetingCardFragment> fragments = new ArrayList<>();

    // 필수 생성자
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public MeetingCardFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    // List에 Fragment를 담을 함수
    public void addItem(MeetingCardFragment fragment) {
        fragments.add(fragment);
    }
    public void clear() {
        for(int i = 0; i < fragments.size(); i++) {
            fragments.remove(i);
        }
        fragments.clear();
    }
}