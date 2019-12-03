package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yamigu.yamigu_app.Fragment.ReceivedMeetingFragment;
import com.yamigu.yamigu_app.Fragment.ReviewFragment;
import com.yamigu.yamigu_app.Fragment.SafeMeetingFragment;
import com.yamigu.yamigu_app.Fragment.SentMeetingFragment;
import com.yamigu.yamigu_app.Fragment.WhatisYamiguFragment;
import com.yamigu.yamigu_app.R;

public class WhatisYamiguActivity extends AppCompatActivity {
    private Toolbar tb;
    private ViewPager pager;
    private Button btn_tab1, btn_tab2, btn_tab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_whatis_yamigu);
        tb = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(tb) ;
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setNavigationIcon(R.drawable.arrow_back_ios);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
        btn_tab1 = findViewById(R.id.btn_tab1);
        btn_tab2 = findViewById(R.id.btn_tab2);
        btn_tab3 = findViewById(R.id.btn_tab3);
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);
        View.OnClickListener movePageListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int tag = (int)view.getTag();
                pager.setCurrentItem(tag);
            }
        };
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch(i) {
                    case 0:
                        btn_tab1.setTextColor(getResources().getColor(R.color.colorPoint));
                        btn_tab2.setTextColor(getResources().getColor(R.color.colorNonselect));
                        btn_tab3.setTextColor(getResources().getColor(R.color.colorNonselect));
                        break;
                    case 1:
                        btn_tab1.setTextColor(getResources().getColor(R.color.colorNonselect));
                        btn_tab2.setTextColor(getResources().getColor(R.color.colorPoint));
                        btn_tab3.setTextColor(getResources().getColor(R.color.colorNonselect));
                        break;
                    case 2:
                        btn_tab1.setTextColor(getResources().getColor(R.color.colorNonselect));
                        btn_tab2.setTextColor(getResources().getColor(R.color.colorNonselect));
                        btn_tab3.setTextColor(getResources().getColor(R.color.colorPoint));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        btn_tab1.setOnClickListener(movePageListener);
        btn_tab1.setTag(0);
        btn_tab2.setOnClickListener(movePageListener);
        btn_tab2.setTag(1);
        btn_tab3.setOnClickListener(movePageListener);
        btn_tab3.setTag(2);
        pager.setCurrentItem(0);
        btn_tab1.setTextColor(getResources().getColor(R.color.colorPoint));
        btn_tab2.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_tab3.setTextColor(getResources().getColor(R.color.colorNonselect));

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = ((GlobalApplication)getApplicationContext()).getCurrentActivity();
        if (this.equals(currActivity))
            ((GlobalApplication)getApplicationContext()).setCurrentActivity(null);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
    private class PagerAdapter extends FragmentStatePagerAdapter {
        WhatisYamiguFragment mf1;
        SafeMeetingFragment mf2;
        ReviewFragment mf3;


        public PagerAdapter(FragmentManager fm) {
            super(fm);
            mf1 = new WhatisYamiguFragment();
            mf2 = new SafeMeetingFragment();
            mf3 = new ReviewFragment();
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return mf1;
                case 1:
                    return mf2;
                case 2:
                    return mf3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }
    }
}
