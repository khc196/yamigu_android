package com.yamigu.yamigu_app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.Activity.MeetingApplicationActivity;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard_Chat;
import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.Activity.TicketOnboardingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    private Toolbar tb;
    private String auth_token;
    private SharedPreferences preferences;
    private Context context;
    MyMeetingCardFrame myMeetingCardFrame;
    private RelativeLayout btn_go_yamigu;

    private class MyMeetingCardFrame {
        private MyMeetingCard mmc_list[];
        private MyMeetingCard_Chat mmc_c_list[];
        private int active_length;
        public MyMeetingCardFrame(View view) {
            mmc_list = new MyMeetingCard[3];
            mmc_c_list = new MyMeetingCard_Chat[3];
            mmc_list[0] = (MyMeetingCard) view.findViewById(R.id.mmc_1);
            mmc_list[1] = (MyMeetingCard) view.findViewById(R.id.mmc_2);
            mmc_list[2] = (MyMeetingCard) view.findViewById(R.id.mmc_3);

            mmc_c_list[0] = (MyMeetingCard_Chat) view.findViewById(R.id.mmc_c_1);
            mmc_c_list[1] = (MyMeetingCard_Chat) view.findViewById(R.id.mmc_c_2);
            mmc_c_list[2] = (MyMeetingCard_Chat) view.findViewById(R.id.mmc_c_3);

            for(int i = 0; i < mmc_list.length; i++) {
                mmc_list[i].setVisibility(View.GONE);
            }
            for(int i = 0; i < mmc_c_list.length; i++) {
                mmc_c_list[i].setVisibility(View.GONE);
            }
        }

        public int getActive_length() {
            return active_length;
        }
        public void setActive_length(int length) {
            active_length = length;
        }
        public void initialize() {

        }
        public MyMeetingCard[] getMmc_list() {
            return mmc_list;
        }

        public MyMeetingCard_Chat[] getMmc_c_list() {
            return mmc_c_list;
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth_token = preferences.getString("auth_token", "");

        tb = (Toolbar) view.findViewById(R.id.toolbar) ;
        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        btn_go_yamigu = (RelativeLayout) view.findViewById(R.id.btn_go_yamigu);
        btn_go_yamigu.setVisibility(View.GONE);
        btn_go_yamigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MeetingApplicationActivity.class);
                startActivity(intent);
            }
        });
        String url = "http://147.47.208.44:9999/api/meetings/my/";
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();

        String url2 = "http://147.47.208.44:9999/api/meetings/my_past/";
        ContentValues values2 = new ContentValues();
        NetworkTask2 networkTask2 = new NetworkTask2(url2, values2);
        networkTask2.execute();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        myMeetingCardFrame = new MyMeetingCardFrame(getView());
        String url = "http://147.47.208.44:9999/api/meetings/my/";
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_items, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.menu_ticket).getActionView();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_ticket:
                Intent intent = new Intent(context, TicketOnboardingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                return true;
        }
        return true;
    }
    private void createPastMeetingCard(final JSONObject json_data) {
        try {
            LinearLayout mRootLinear = (LinearLayout) getView().findViewById(R.id.past_meeting_root);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            final View mmca = inflater.inflate(R.layout.my_meeting_card_after, mRootLinear, false);
            mRootLinear.addView(mmca);
            final LinearLayout first_pane = mmca.findViewById(R.id.first_pane);
            final RelativeLayout second_pane = mmca.findViewById(R.id.second_pane);
            final FrameLayout third_pane = mmca.findViewById(R.id.third_pane);
            final LinearLayout forth_pane = mmca.findViewById(R.id.forth_pane);
            first_pane.setVisibility(View.VISIBLE);
            second_pane.setVisibility(View.GONE);
            third_pane.setVisibility(View.GONE);
            forth_pane.setVisibility(View.GONE);

            //first pane
            TextView tv_how_were = mmca.findViewById(R.id.tv_how_were);
            final Button btn_go_review = mmca.findViewById(R.id.btn_go_review);

            //second pane
            final Button btn_stars_visual[] = new Button[5];
            final Button btn_stars_fun[] = new Button[5];
            final Button btn_stars_manner[] = new Button[5];
            btn_stars_visual[0] = mmca.findViewById(R.id.btn_star_1);
            btn_stars_visual[1] = mmca.findViewById(R.id.btn_star_2);
            btn_stars_visual[2] = mmca.findViewById(R.id.btn_star_3);
            btn_stars_visual[3] = mmca.findViewById(R.id.btn_star_4);
            btn_stars_visual[4] = mmca.findViewById(R.id.btn_star_5);
            btn_stars_fun[0] = mmca.findViewById(R.id.btn_star_6);
            btn_stars_fun[1] = mmca.findViewById(R.id.btn_star_7);
            btn_stars_fun[2] = mmca.findViewById(R.id.btn_star_8);
            btn_stars_fun[3] = mmca.findViewById(R.id.btn_star_9);
            btn_stars_fun[4] = mmca.findViewById(R.id.btn_star_10);
            btn_stars_manner[0] = mmca.findViewById(R.id.btn_star_11);
            btn_stars_manner[1] = mmca.findViewById(R.id.btn_star_12);
            btn_stars_manner[2] = mmca.findViewById(R.id.btn_star_13);
            btn_stars_manner[3] = mmca.findViewById(R.id.btn_star_14);
            btn_stars_manner[4] = mmca.findViewById(R.id.btn_star_15);


            //third pane
            EditText et_feedback = mmca.findViewById(R.id.et_feedback);
            Button btn_send_feedback = mmca.findViewById(R.id.btn_send_feedback);
            TextView tv_skip_feedback = mmca.findViewById(R.id.tv_skip_feedback);

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(json_data.getString("date"));
                tv_how_were.setText((date.getMonth()+1)+"월 "+ date.getDate() + "일 " + "미팅 어떠셨나요?!");
            } catch(ParseException e) {
                e.printStackTrace();
            }

            btn_go_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                    AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                    fadeIn.setDuration(1000);
                    fadeOut.setDuration(1000);
                    fadeIn.setFillAfter(true);
                    fadeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            first_pane.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            second_pane.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    first_pane.startAnimation(fadeOut);
                    second_pane.startAnimation(fadeIn);
                }
            });
            for (int i = 0; i < 5; i++) {
                final int position = i;
                final Button button_visual = btn_stars_visual[i];
                final Button button_fun = btn_stars_fun[i];
                final Button button_manner = btn_stars_manner[i];
                button_visual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (button_visual.getText().toString().equals("★")) {
                            boolean flag = false;
                            for (int j = position + 1; j < 5; j++) {
                                if (btn_stars_visual[j].getText().toString().equals("★")) {
                                    btn_stars_visual[j].setText("☆");
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                for (int j = 0; j <= position; j++) {
                                    btn_stars_visual[j].setText("☆");
                                }
                            }
                        } else {
                            boolean flag1 = false;
                            boolean flag2 = false;
                            for (int j = 0; j <= position; j++) {
                                btn_stars_visual[j].setText("★");
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_fun[k].getText().toString().equals("★")) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_manner[k].getText().toString().equals("★")) {
                                    flag2 = true;
                                    break;
                                }
                            }
                            if(flag1 && flag2) {
                                AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                                AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                                fadeIn.setDuration(1000);
                                fadeOut.setDuration(1000);
                                fadeIn.setFillAfter(true);
                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        second_pane.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        third_pane.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                third_pane.startAnimation(fadeIn);
                                second_pane.startAnimation(fadeOut);
                            }
                        }
                    }
                });
                button_fun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (button_fun.getText().toString().equals("★")) {
                            boolean flag = false;
                            for (int j = position + 1; j < 5; j++) {
                                if (btn_stars_fun[j].getText().toString().equals("★")) {
                                    btn_stars_fun[j].setText("☆");
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                for (int j = 0; j <= position; j++) {
                                    btn_stars_fun[j].setText("☆");
                                }
                            }
                        } else {
                            boolean flag1 = false;
                            boolean flag2 = false;
                            for (int j = 0; j <= position; j++) {
                                btn_stars_fun[j].setText("★");
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_visual[k].getText().toString().equals("★")) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_manner[k].getText().toString().equals("★")) {
                                    flag2 = true;
                                    break;
                                }
                            }
                            if(flag1 && flag2) {
                                AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                                AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                                fadeIn.setDuration(1000);
                                fadeOut.setDuration(1000);
                                fadeIn.setFillAfter(true);
                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        second_pane.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        third_pane.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                third_pane.startAnimation(fadeIn);
                                second_pane.startAnimation(fadeOut);
                            }
                        }
                    }
                });
                button_manner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (button_manner.getText().toString().equals("★")) {
                            boolean flag = false;
                            for (int j = position + 1; j < 5; j++) {
                                if (btn_stars_manner[j].getText().toString().equals("★")) {
                                    btn_stars_manner[j].setText("☆");
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                for (int j = 0; j <= position; j++) {
                                    btn_stars_manner[j].setText("☆");
                                }
                            }
                        } else {
                            boolean flag1 = false;
                            boolean flag2 = false;
                            for (int j = 0; j <= position; j++) {
                                btn_stars_manner[j].setText("★");
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_visual[k].getText().toString().equals("★")) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_fun[k].getText().toString().equals("★")) {
                                    flag2 = true;
                                    break;
                                }
                            }
                            if(flag1 && flag2) {
                                AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                                AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                                fadeIn.setDuration(1000);
                                fadeOut.setDuration(1000);
                                fadeIn.setFillAfter(true);
                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        second_pane.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        third_pane.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                third_pane.startAnimation(fadeIn);
                                second_pane.startAnimation(fadeOut);
                            }
                        }
                    }
                });
                btn_send_feedback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeIn.setDuration(1000);
                        fadeOut.setDuration(1000);
                        fadeIn.setFillAfter(true);
                        fadeIn.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                third_pane.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                forth_pane.setVisibility(View.VISIBLE);
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        forth_pane.startAnimation(fadeIn);
                        third_pane.startAnimation(fadeOut);

                        Runnable mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                mmca.animate()
                                        .translationY(-1000)
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                mmca.setVisibility(View.GONE);
                                            }});
                            }
                        };

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 3000);
                    }
                });
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
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

            result = requestHttpURLConnection.request(context, url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            myMeetingCardFrame.setActive_length(jsonArray.length());
            if(myMeetingCardFrame.getActive_length() == 0) {
                btn_go_yamigu.setVisibility(View.VISIBLE);
            }
            else {
                btn_go_yamigu.setVisibility(View.GONE);
            }
            for(int i = 0; i < myMeetingCardFrame.getActive_length(); i++) {
                try {
                    myMeetingCardFrame.mmc_list[i].setId(jsonArray.getJSONObject(i).getInt("id"));
                    myMeetingCardFrame.mmc_list[i].setType(jsonArray.getJSONObject(i).getInt("meeting_type"));
                    myMeetingCardFrame.mmc_list[i].setPlace(jsonArray.getJSONObject(i).getInt("place_type"));
                    myMeetingCardFrame.mmc_list[i].setPlaceString(jsonArray.getJSONObject(i).getString("place_type_name"));
                    myMeetingCardFrame.mmc_list[i].setNum_of_applying(jsonArray.getJSONObject(i).getInt("received_request"));
                    myMeetingCardFrame.mmc_list[i].setAppeal(jsonArray.getJSONObject(i).getString("appeal"));
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date translated_date;
                    Date today = new Date();
                    Calendar cal_meeting_day = Calendar.getInstance();
                    Calendar cal_today = Calendar.getInstance();
                    try {
                        translated_date = transFormat.parse(jsonArray.getJSONObject(i).getString("date"));
                        cal_meeting_day.setTime(translated_date);
                        cal_today.setTime(today);
                        long l_mday = cal_meeting_day.getTimeInMillis() / (24 * 60 * 60 * 1000);
                        long l_tday = cal_today.getTimeInMillis() / (24 * 60 * 60 * 1000);

                        myMeetingCardFrame.mmc_list[i].setMonth(translated_date.getMonth());
                        myMeetingCardFrame.mmc_list[i].setDate(translated_date.getDate());
                        myMeetingCardFrame.mmc_list[i].setDday((int)(l_mday - l_tday + 1));
                        if(jsonArray.getJSONObject(i).getBoolean("is_matched")) {
                            JSONObject matched_meeting = jsonArray.getJSONObject(i).getJSONObject("matched_meeting");
                            myMeetingCardFrame.mmc_c_list[i].setVisibility(View.VISIBLE);
                            myMeetingCardFrame.mmc_list[i].setProfile1(matched_meeting.getString("openby_nickname"), matched_meeting.getInt("openby_age"));
                            myMeetingCardFrame.mmc_list[i].setProfile2(matched_meeting.getString("openby_belong"), matched_meeting.getString("openby_department"));
                            myMeetingCardFrame.mmc_list[i].setMatched(true);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                myMeetingCardFrame.mmc_list[i].setVisibility(View.VISIBLE);
            }
        }
    }
    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask2(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(context, url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                if(jsonArray.length() > 0) {
                    for(int i = 0; i < jsonArray.length(); i++) {
                        createPastMeetingCard(jsonArray.getJSONObject(i));
                    }
                }
                else {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
