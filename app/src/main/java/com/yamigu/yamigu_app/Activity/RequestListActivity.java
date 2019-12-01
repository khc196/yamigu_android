package com.yamigu.yamigu_app.Activity;


import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yamigu.yamigu_app.Fragment.ReceivedMeetingFragment;
import com.yamigu.yamigu_app.Fragment.SentMeetingFragment;
import com.yamigu.yamigu_app.R;

import java.util.ArrayList;

public class RequestListActivity extends AppCompatActivity {

    private Toolbar tb;
    private TextView tv_title;
    private ViewPager pager;
    private Button btn_recv, btn_sent;

    private String auth_token;
    int meeting_id;
    private SharedPreferences preferences;
    private ArrayList<Integer> received_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        auth_token = preferences.getString("auth_token", "");
        String date = intent.getExtras().getString("date");
        String place = intent.getExtras().getString("place");
        String type = intent.getExtras().getString("type");
        meeting_id = intent.getExtras().getInt("meeting_id");

        tv_title = findViewById(R.id.title);
        pager = (ViewPager)findViewById(R.id.pager);
        btn_recv = (Button)findViewById(R.id.btn_received);
        btn_sent = (Button)findViewById(R.id.btn_sent);

        tv_title.setText(date + " || " + place + " || " + type);
        pager.setAdapter(new pagerAdapter(getSupportFragmentManager()));

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
                        btn_recv.setTextColor(getResources().getColor(R.color.colorPoint));
                        btn_sent.setTextColor(getResources().getColor(R.color.colorNonselect));
                        break;
                    case 1:
                        btn_recv.setTextColor(getResources().getColor(R.color.colorNonselect));
                        btn_sent.setTextColor(getResources().getColor(R.color.colorPoint));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        btn_recv.setOnClickListener(movePageListener);
        btn_recv.setTag(0);
        btn_sent.setOnClickListener(movePageListener);
        btn_sent.setTag(1);
        pager.setCurrentItem(0);
        btn_recv.setTextColor(getResources().getColor(R.color.colorPoint));
        btn_sent.setTextColor(getResources().getColor(R.color.colorNonselect));

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
    private class pagerAdapter extends FragmentStatePagerAdapter {
        ReceivedMeetingFragment mf1;
        SentMeetingFragment mf2;

        public pagerAdapter(FragmentManager fm) {
            super(fm);
            mf1 = new ReceivedMeetingFragment();
            mf2 = new SentMeetingFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("meeting_id", meeting_id);
            mf1.setArguments(bundle);
            mf2.setArguments(bundle);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;
            switch (position) {
                case 0:
                    return mf1;
                case 1:
                    return mf2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (position == 0)
                mf1.refresh(); //Refresh what you need on this fragment
            else if (position == 1)
                mf2.refresh();
        }
    }
}
