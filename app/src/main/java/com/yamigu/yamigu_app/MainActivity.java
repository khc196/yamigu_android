package com.yamigu.yamigu_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment;
    private ImageButton nav_home, nav_wlist, nav_yamigu, nav_mypage, nav_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nav_home = (ImageButton) findViewById(R.id.nav_home);
        nav_wlist = (ImageButton) findViewById(R.id.nav_wlist);
        nav_yamigu = (ImageButton) findViewById(R.id.nav_yamigu);
        nav_mypage = (ImageButton) findViewById(R.id.nav_mypage);
        nav_more = (ImageButton) findViewById(R.id.nav_more);
        nav_yamigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MeetingApplicationActivity.class);
                startActivity(intent);
            }
        });
        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nav_home.setImageResource(R.drawable.nav_home_selected);
                nav_wlist.setImageResource(R.drawable.nav_wlist);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more);
                loadFragment(homeFragment);
            }
        });
        nav_wlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nav_home.setImageResource(R.drawable.nav_home);
                nav_wlist.setImageResource(R.drawable.nav_wlist_selected);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more);
                Fragment fragment = new WListFragment();
                loadFragment(fragment);
            }
        });
        nav_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nav_home.setImageResource(R.drawable.nav_home);
                nav_wlist.setImageResource(R.drawable.nav_wlist);
                nav_mypage.setImageResource(R.drawable.nav_mypage_selected);
                nav_more.setImageResource(R.drawable.nav_more);
                Fragment fragment = new MypageFragment();
                loadFragment(fragment);
            }
        });
        nav_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nav_home.setImageResource(R.drawable.nav_home);
                nav_wlist.setImageResource(R.drawable.nav_wlist);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more_selected);
                Fragment fragment = new MoreFragment();
                loadFragment(fragment);
            }
        });

        //BottomNavigationView navigation = findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(this);
        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        Fragment fragment = null;
//
//        switch(item.getItemId()) {
//            case R.id.navigation_home:
//                fragment = new HomeFragment();
//                break;
//            case R.id.navigation_list:
//                fragment = new WListFragment();
//                break;
//            case R.id.navigation_yamigu:
//                Intent intent = new Intent(this, MeetingApplicationActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.navigation_mypage:
//                fragment = new MypageFragment();
//                break;
//            case R.id.navigation_more:
//                fragment = new MoreFragment();
//                break;
//        }
//        return loadFragment(fragment);
//    }
}
