package com.yamigu.yamigu_app.Activity;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.util.Base64;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yamigu.yamigu_app.Adapter.MainFragmentAdapter;
import com.yamigu.yamigu_app.CustomLayout.CustomDialog2;
import com.yamigu.yamigu_app.Etc.ImageUtils;
import com.yamigu.yamigu_app.Fragment.HomeFragment;
import com.yamigu.yamigu_app.Fragment.MoreFragment;
import com.yamigu.yamigu_app.Fragment.MypageFragment;
import com.yamigu.yamigu_app.Fragment.WListFragment;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment;
    private LinearLayout nav_bar;
    public ImageButton nav_home, nav_wlist, nav_yamigu, nav_mypage, nav_more;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static int count_meeting;
    private DatabaseReference userDB;
    public static MainActivity me;
    public static ProgressDialog dialog = null;
    private static CustomDialog2 popupDialog = null;
    ViewPager pager;
    MainFragmentAdapter mainFragmentAdapter;
    public static androidx.appcompat.widget.Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb = findViewById(R.id.toolbar) ;
        setSupportActionBar(tb) ;
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        setHasOptionsMenu(true);
        me = this;
        Intent intent = getIntent();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(preferences.getString("uid", ""));
        auth_token = preferences.getString("auth_token", "");

        String url = preferences.getString("profile", "");
        if(!url.isEmpty()) {
            ContentValues values = new ContentValues();
            NetworkTask networkTask = new NetworkTask(url, values);
            networkTask.execute();
        }
        nav_bar = (LinearLayout) findViewById(R.id.nav_bar);
        nav_home = (ImageButton) findViewById(R.id.nav_home);
        nav_wlist = (ImageButton) findViewById(R.id.nav_wlist);
        nav_yamigu = (ImageButton) findViewById(R.id.nav_yamigu);
        nav_mypage = (ImageButton) findViewById(R.id.nav_mypage);
        nav_more = (ImageButton) findViewById(R.id.nav_more);
        pager = findViewById(R.id.fragment_container);

        mainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        MainActivity.dialog = ProgressDialog.show(this, "", "로딩중입니다...", true);
        mainFragmentAdapter.addItem(new HomeFragment());
        mainFragmentAdapter.addItem(new WListFragment());
        mainFragmentAdapter.addItem(new MypageFragment());
        mainFragmentAdapter.addItem(new MoreFragment());
        pager.setAdapter(mainFragmentAdapter);

        nav_yamigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count_meeting < 3) {
                    Intent intent = new Intent(view.getContext(), MeetingApplicationActivity.class);
                    startActivity(intent);
                }
                else {
                    setDialog("미팅은 일주일에 3번까지만 가능해요!");
                    showDialog();
                }
            }
        });
        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(1);
                //loadFragment(homeFragment);
            }
        });
        nav_wlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(2);
                //Fragment fragment = new WListFragment();
                //loadFragment(fragment);
            }
        });
        nav_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(3);
                //Fragment fragment = new MypageFragment();
                //loadFragment(fragment);
            }
        });
        nav_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(4);
                //Fragment fragment = new MoreFragment();
                //loadFragment(fragment);
            }
        });

        //homeFragment = new HomeFragment();
        //loadFragment(homeFragment);

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
    public static void setDialog(String text) {
        popupDialog = new CustomDialog2(me);
        popupDialog.setCancelable(true);
        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.text = text;

        popupDialog.getWindow().setGravity(Gravity.CENTER);


    }
    public static void showDialog() {
        popupDialog.show();
    }

    public boolean loadFragment(Fragment fragment) {
        if(fragment != null) {

            if(dialog == null || !dialog.isShowing())
                MainActivity.dialog = ProgressDialog.show(this, "", "로딩중입니다...", true);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    public void refresh() {
        if(mainFragmentAdapter != null) {
            mainFragmentAdapter.notifyDataSetChanged();
        }
    }
    public void selectTab(int index) {
        // MainActivity.dialog = ProgressDialog.show(this, "", "로딩중입니다...", true);
        pager.setCurrentItem(index-1);
        switch(index) {
            case 1:
                nav_home.setImageResource(R.drawable.nav_home_selected);
                nav_wlist.setImageResource(R.drawable.nav_wlist);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more);
                break;
            case 2:
                nav_home.setImageResource(R.drawable.nav_home);
                nav_wlist.setImageResource(R.drawable.nav_wlist_selected);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more);
                break;
            case 3:
                nav_home.setImageResource(R.drawable.nav_home);
                nav_wlist.setImageResource(R.drawable.nav_wlist);
                nav_mypage.setImageResource(R.drawable.nav_mypage_selected);
                nav_more.setImageResource(R.drawable.nav_more);
                break;
            case 4:
                nav_home.setImageResource(R.drawable.nav_home);
                nav_wlist.setImageResource(R.drawable.nav_wlist);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more_selected);
                break;
            default:
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
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
    public static int getMyMeetingCount() {
        return count_meeting;
    }
    public class NetworkTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlO = new URL(url);

                URLConnection conn = urlO.openConnection();
                conn.connect();
                InputStream urlInputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(urlInputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            GlobalApplication.bitmap_map.put(url, bm);
        }
    }
}
