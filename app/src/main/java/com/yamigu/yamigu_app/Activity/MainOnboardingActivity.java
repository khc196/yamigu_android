package com.yamigu.yamigu_app.Activity;


import android.app.Activity;
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;

import com.yamigu.yamigu_app.CustomLayout.MainOnboardingItem;
import com.yamigu.yamigu_app.Adapter.MainOnboardingPagerAdapter;
import com.yamigu.yamigu_app.R;

import java.util.ArrayList;
import java.util.List;

public class MainOnboardingActivity extends AppCompatActivity {
    private ViewPager screenPager;
    MainOnboardingPagerAdapter mainOnboardingPagerAdapter;
    TabLayout tabIndicator;
    ImageButton btn_start;
    int tabsize;
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_onboarding);

        //
        tabIndicator = findViewById(R.id.tab_indicator);
        List<MainOnboardingItem> mList = new ArrayList<>();
        mList.add(new MainOnboardingItem("안전한 만남!", "이름, 나이, 소속 인증을 통해", "확실하고 안전한 만남이 이뤄져요.", R.drawable.onboarding1));
        mList.add(new MainOnboardingItem("간편한 만남!", "미리 날짜와 장소를 정해", "매칭 이후에는 만나기만 하세요.", R.drawable.onboarding2));
        mList.add(new MainOnboardingItem("D-7 미팅!", "오늘부터 일주일뒤까지", "7일 안에 빠르게 미팅하세요.", R.drawable.onboarding3));
        mList.add(new MainOnboardingItem("미팅 매니저와 함께!", "미팅 신청부터 진행까지", "미팅 매니저가 만남을 도와줘요.", R.drawable.onboarding4));
        tabsize = mList.size();
        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        mainOnboardingPagerAdapter = new MainOnboardingPagerAdapter(this, mList);
        screenPager.setAdapter(mainOnboardingPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);
        btn_start = findViewById(R.id.btn_start);
        btn_start.setVisibility(View.INVISIBLE);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);

                finish();
            }
        });
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == tabsize - 1) {
                    btn_start.setVisibility(View.VISIBLE);
                }
                else {
                    btn_start.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        screenPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            boolean lastPageChange = false;
            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int lastIdx = mainOnboardingPagerAdapter.getCount() - 1;

                if(lastPageChange && position == lastIdx) {
                    // next

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int lastIdx = mainOnboardingPagerAdapter.getCount() - 1;

                int curItem = screenPager.getCurrentItem();
                if(curItem==lastIdx /*&& lastPos==lastIdx*/  && state==1) {
                    lastPageChange = true;
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                    finish();
                } else {
                    lastPageChange = false;
                }
            }
        });

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
}
