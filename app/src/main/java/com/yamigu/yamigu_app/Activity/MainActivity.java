package com.yamigu.yamigu_app.Activity;

import android.Manifest;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.util.Base64;
import android.util.Log;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yamigu.yamigu_app.Adapter.MainFragmentAdapter;
import com.yamigu.yamigu_app.CustomLayout.CustomDialog2;
import com.yamigu.yamigu_app.CustomLayout.CustomViewPager;
import com.yamigu.yamigu_app.CustomLayout.CustomViewPager2;
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
    public static ImageButton nav_home, nav_wlist, nav_yamigu, nav_mypage, nav_more;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static int count_meeting;
    private DatabaseReference userDB;
    public static MainActivity me;
    public static ProgressDialog dialog = null;
    private static CustomDialog2 popupDialog = null;
    public static CustomViewPager2 pager;
    public static MainFragmentAdapter mainFragmentAdapter;
    public static androidx.appcompat.widget.Toolbar tb;
    private static int cur_index = 0;
    public static TextView tv_unread_noti_count, tv_ticket_count;
    private long time= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        setContentView(R.layout.activity_main);
        findViewById(R.id.overall_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GlobalApplication.hideKeyboard(MainActivity.this);
                return false;
            }
        });
        tv_unread_noti_count = findViewById(R.id.unread_noti_count);
        tv_ticket_count = findViewById(R.id.ticket_count);
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

        nav_bar = (LinearLayout) findViewById(R.id.nav_bar);
        nav_home = (ImageButton) findViewById(R.id.nav_home);
        nav_wlist = (ImageButton) findViewById(R.id.nav_wlist);
        nav_yamigu = (ImageButton) findViewById(R.id.nav_yamigu);
        nav_mypage = (ImageButton) findViewById(R.id.nav_mypage);
        nav_more = (ImageButton) findViewById(R.id.nav_more);
        pager = findViewById(R.id.fragment_container);
        pager.setPagingEnabled(false);
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
                    int ticket_count = preferences.getInt("num_of_ticket", 0);
                    if(ticket_count == 0) {
                        setDialog("티켓이 있어야 미팅을 할 수 있어요! \n" +
                                "단, 매칭이 안되면 티켓은 돌려줘요.");
                        showDialog();
                    }
                    else {
                        dialog = ProgressDialog.show(MainActivity.this, "", "로딩중입니다...", true);
                        Intent intent = new Intent(view.getContext(), MeetingApplicationActivity.class);
                        intent.putExtra("form_code", MeetingApplicationActivity.NEW_MEETING);
                        startActivity(intent);
                    }
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
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mainFragmentAdapter.getItem(cur_index).setUserVisibleHint(false);
                mainFragmentAdapter.getItem(cur_index).onPause();
                cur_index = position;
                mainFragmentAdapter.getItem(cur_index).setUserVisibleHint(true);
                mainFragmentAdapter.getItem(cur_index).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setOffscreenPageLimit(3);
        if(!url.isEmpty()) {
            ContentValues values = new ContentValues();
            NetworkTask networkTask = new NetworkTask(url, values);
            networkTask.execute();
        }
        pager.setCurrentItem(0, false);
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
    public static void selectTab(int index) {
        // MainActivity.dialog = ProgressDialog.show(this, "", "로딩중입니다...", true);
        switch(index) {
            case 1:
                nav_home.setImageResource(R.drawable.nav_home_selected);
                nav_wlist.setImageResource(R.drawable.nav_wlist);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more);
                //((HomeFragment)mainFragmentAdapter.getItem(index-1)).refresh();
                break;
            case 2:
                nav_home.setImageResource(R.drawable.nav_home);
                nav_wlist.setImageResource(R.drawable.nav_wlist_selected);
                nav_mypage.setImageResource(R.drawable.nav_mypage);
                nav_more.setImageResource(R.drawable.nav_more);
                //((WListFragment)mainFragmentAdapter.getItem(index-1)).refresh();
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
        tv_unread_noti_count.setVisibility(View.INVISIBLE);
        tv_ticket_count.setVisibility(View.INVISIBLE);
        pager.setCurrentItem(index-1, false);

    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        if(pager.getCurrentItem() == 0) {
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
            } else if (System.currentTimeMillis() - time < 2000) {
                finish();
            }
        }
        else {
            selectTab(1) ;
        }
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
            try {
                MypageFragment.profile_img.setImageBitmap(GlobalApplication.bitmap_map.get(url));
            } catch(NullPointerException e) {

            }
        }
    }
}
