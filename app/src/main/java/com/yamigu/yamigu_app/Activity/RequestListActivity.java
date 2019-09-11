package com.yamigu.yamigu_app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.CustomLayout.CircularImageView;
import com.yamigu.yamigu_app.Fragment.MeetingCardFragment;
import com.yamigu.yamigu_app.Fragment.WListFragment;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.PagerAdapter.FragmentAdapter;
import com.yamigu.yamigu_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class RequestListActivity extends AppCompatActivity {

    private Toolbar tb;
    private TextView tv_title, tv_current, tv_total;
    private FragmentAdapter fragmentAdapter;
    private ViewPager viewPager;
    private boolean is_initialized = false;
    private String auth_token;
    int dpValue = 0;
    private int total_num = 0;
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
        Intent intent = getIntent();
        auth_token = intent.getExtras().getString("auth_token");
        String date = intent.getExtras().getString("date");
        String place = intent.getExtras().getString("place");
        String type = intent.getExtras().getString("type");
        int meeting_id = intent.getExtras().getInt("meeting_id");
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
                for(int i = 1; i <= total_num; i++) {
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
                if(is_initialized) {
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

                }
            }
        });

        viewPager.setClipToPadding(false);

        String url = "http://147.47.208.44:9999/api/meetings/request_match/?meeting_id="+meeting_id;
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }

    private void refresh() {
        fragmentAdapter.notifyDataSetChanged();
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                total_num = jsonArray.length();
                tv_total.setText(Integer.toString(total_num));
                for(int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json_data = jsonArray.getJSONObject(i).getJSONObject("sender");
                    Log.d("onPostExecute", json_data.toString());

                    MeetingCardFragment meetingCardFragment = new MeetingCardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", json_data.getInt("meeting_type"));
                    bundle.putString("nickname", json_data.getString("openby_nickname"));
                    bundle.putInt("age", json_data.getInt("openby_age"));
                    bundle.putString("belong", json_data.getString("openby_belong"));
                    bundle.putString("department", json_data.getString("openby_department"));
                    bundle.putString("date", json_data.getString("date"));
                    bundle.putString("appeal", json_data.getString("appeal"));
                    bundle.putString("place_type_name", json_data.getString("place_type_name"));
                    bundle.putString("profile_img_url", json_data.getString("openby_profile"));
                    meetingCardFragment.setArguments(bundle);
                    fragmentAdapter.addItem(meetingCardFragment);
                }
                refresh();
                is_initialized = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
