package com.yamigu.yamigu_app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.yamigu.yamigu_app.Fragment.HomeFragment;
import com.yamigu.yamigu_app.Fragment.MoreFragment;
import com.yamigu.yamigu_app.Fragment.MypageFragment;
import com.yamigu.yamigu_app.Fragment.WListFragment;
import com.yamigu.yamigu_app.R;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment;
    private LinearLayout nav_bar;
    public ImageButton nav_home, nav_wlist, nav_yamigu, nav_mypage, nav_more;
    private String auth_token;
    private SharedPreferences preferences;
    private int count_meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getHashKey(getApplicationContext());
        Intent intent = getIntent();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        auth_token = preferences.getString("auth_token", "");

        nav_bar = (LinearLayout) findViewById(R.id.nav_bar);
        nav_home = (ImageButton) findViewById(R.id.nav_home);
        nav_wlist = (ImageButton) findViewById(R.id.nav_wlist);
        nav_yamigu = (ImageButton) findViewById(R.id.nav_yamigu);
        nav_mypage = (ImageButton) findViewById(R.id.nav_mypage);
        nav_more = (ImageButton) findViewById(R.id.nav_more);
        nav_yamigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count_meeting < 3) {
                    Intent intent = new Intent(view.getContext(), MeetingApplicationActivity.class);
                    startActivity(intent);
                }
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

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
    }
    public boolean loadFragment(Fragment fragment) {
        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    public void setMyMeetingCount(int num) {
        int resources[] = {R.drawable.nav_yamigu_0, R.drawable.nav_yamigu_1, R.drawable.nav_yamigu_2, R.drawable.nav_yamigu_3};
        nav_yamigu.setImageResource(resources[num]);
        count_meeting = num;
    }
    @Nullable
    public static String getHashKey(Context context) {

        final String TAG = "KeyHash";

        String keyHash = null;

        try {

            PackageInfo info =

                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);



            for (Signature signature : info.signatures) {

                MessageDigest md;

                md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                keyHash = new String(Base64.encode(md.digest(), 0));

                Log.d(TAG, keyHash);

            }

        } catch (Exception e) {

            Log.e("name not found", e.toString());

        }



        if (keyHash != null) {

            return keyHash;

        } else {

            return null;

        }

    }
}
