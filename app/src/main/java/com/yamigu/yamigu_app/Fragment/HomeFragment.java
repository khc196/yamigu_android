package com.yamigu.yamigu_app.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RelativeLayout;

import com.yamigu.yamigu_app.Activity.MeetingApplicationActivity;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard_Chat;
import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.Activity.TicketOnboardingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}
