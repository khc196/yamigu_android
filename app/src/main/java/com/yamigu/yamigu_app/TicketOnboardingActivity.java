package com.yamigu.yamigu_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class TicketOnboardingActivity extends AppCompatActivity {
    private Toolbar tb;
    private ViewPager screenPager;
    TicketOnboardingPagerAdapter ticketOnboardingPagerAdapter;
    TabLayout tabIndicator;
    Button btn_buy_ticket;
    int tabsize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_onboarding);
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
        //
        tabIndicator = findViewById(R.id.tab_indicator);
        List<TicketOnboardingItem> mList = new ArrayList<>();
        mList.add(new TicketOnboardingItem("새로운 이성과의", "설레는 야미구", "야미구는 채팅이 아닌", "만남을 제공해요.",  R.drawable.onboarding_t1));
        mList.add(new TicketOnboardingItem("프리미엄 제휴매장에서", "기분좋은 야미구", "10000원 상당 제휴 쿠폰 제공", "(*사실상 야미구 티켓 공짜!)", R.drawable.onboarding_t2));
        mList.add(new TicketOnboardingItem("친구들과 부담없이", "즐기는 야미구", "함께 나가는 친구들과 나누세요.", "(*카카오 더치페이 추천)", R.drawable.onboarding_t3));
        mList.add(new TicketOnboardingItem("처음이어도", "괜찮은 야미구", "매니저가 친절하게 알려줄게요.", "(*꿀팁은 덤!)",  R.drawable.onboarding_t4));
        tabsize = mList.size();
        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        ticketOnboardingPagerAdapter = new TicketOnboardingPagerAdapter(this, mList);
        screenPager.setAdapter(ticketOnboardingPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);
        btn_buy_ticket = findViewById(R.id.btn_buy_ticket);
        btn_buy_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TicketActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
