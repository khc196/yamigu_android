package com.yamigu.yamigu_app.Activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.Fragment.MeetingCardFragment;
import com.yamigu.yamigu_app.PagerAdapter.FragmentAdapter;
import com.yamigu.yamigu_app.R;

import java.util.ArrayList;

public class RequestListActivity extends AppCompatActivity {

    private Toolbar tb;
    private TextView tv_title, tv_current, tv_total;
    private FragmentAdapter fragmentAdapter;
    private ViewPager viewPager;
    private boolean is_initialized = false;

    int dpValue = 0;
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
        Intent intent = getIntent();
        String date = intent.getExtras().getString("date");
        String place = intent.getExtras().getString("place");
        String type = intent.getExtras().getString("type");

        tv_title = findViewById(R.id.title);
        tv_current = findViewById(R.id.tv_num_of_recv);
        tv_total = findViewById(R.id.tv_total_of_recv);

        tv_title.setText(date + " || " + place + " || " + type);
        tv_total.setText(Integer.toString(3));
        tv_current.setText(Integer.toString(1));
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                MeetingCardFragment fragment;
                int currentPage = viewPager.getCurrentItem();
                tv_current.setText(Integer.toString(currentPage + 1));
                for(int i = 1; i <= 3; i++) {
                    float d = getResources().getDisplayMetrics().density;
                    fragment = fragmentAdapter.getItem(i - 1);
                    LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) fragment.waitingTeamCard.getLayoutParams();
                    if(i - 1 == currentPage) {
                        mLayoutParams.topMargin = 0;
                        fragment.waitingTeamCard.setAlpha(1.0f);
                        fragment.ll_btn_layout.setVisibility(View.VISIBLE);
                    }
                    else {
                        mLayoutParams.topMargin = Math.round(16 * d);
                        fragment.waitingTeamCard.setAlpha(0.4f);
                        fragment.ll_btn_layout.setVisibility(View.INVISIBLE);
                    }
                    fragment.waitingTeamCard.setLayoutParams(mLayoutParams);
                }
                refresh();
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }

        });
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        ViewTreeObserver vto = viewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!is_initialized) {
                    int width = viewPager.getWidth();
                    float d = getResources().getDisplayMetrics().density;
                    dpValue = (int) (width / d);
                    int margin = (int) (45 * dpValue / 411 * d);
                    viewPager.setPadding(margin, 0, margin, 0);
                    MeetingCardFragment fragment = fragmentAdapter.getItem(0);
                    LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) fragment.waitingTeamCard.getLayoutParams();
                    mLayoutParams.topMargin = 0;
                    fragment.waitingTeamCard.setAlpha(1.0f);
                    fragment.waitingTeamCard.setLayoutParams(mLayoutParams);
                    fragment.ll_btn_layout.setVisibility(View.VISIBLE);
                    is_initialized = true;
                }
            }
        });

        viewPager.setClipToPadding(false);


        //viewPager.setPageMargin(margin/2);

        ArrayList<Integer> received_list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            MeetingCardFragment meetingCardFragment = new MeetingCardFragment();
            Bundle bundle = new Bundle();
            //bundle.putInt("id", received_list.get(i));
            meetingCardFragment.setArguments(bundle);
            fragmentAdapter.addItem(meetingCardFragment);
        }
        refresh();
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }

    private void refresh() {
        fragmentAdapter.notifyDataSetChanged();
    }
}
