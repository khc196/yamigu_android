package com.yamigu.yamigu_app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WListFragment extends Fragment {
    private Toolbar tb;
    private String auth_token;
    private Button[] btn_date_list;
    public static String[] date_list;
    public static Set<String> active_date_set;
    public static Set<Integer> active_place_set;
    public static Set<Integer> active_type_set;
    public static int minimum_age = 0;
    public static int maximum_age = 11;
    private LayoutInflater mInflater;
    private View view;
    static int id = 1;
    private boolean is_initialized = false;
    public WListFragment() {
        this.active_type_set = new HashSet<>();
        this.active_place_set = new HashSet<>();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        view = inflater.inflate(R.layout.fragment_wlist, container, false);
        auth_token = getActivity().getIntent().getExtras().getString("auth_token");
        tb = (Toolbar) view.findViewById(R.id.toolbar) ;
        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("M/d");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");


        btn_date_list = new Button[7];
        date_list = new String[7];
        int[] btn_date_id_list = {R.id.btn_date_1, R.id.btn_date_2, R.id.btn_date_3, R.id.btn_date_4, R.id.btn_date_5, R.id.btn_date_6, R.id.btn_date_7};
        for(int i = 0; i < btn_date_list.length; i++) {
            final TextView btn_date = (TextView) view.findViewById(btn_date_id_list[i]);
            String getTime = sdf.format(cal.getTime());
            String getTime2 = sdf2.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            date_list[i] = getTime2;
            btn_date.setText(getTime);
            btn_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!is_initialized) return;
                    String btn_text_before = btn_date.getText().toString();
                    String btn_text = "";
                    Date now = new Date();
                    Date from = null;
                    try {
                         from = new SimpleDateFormat("M/d").parse(btn_text_before);
                         from.setYear(now.getYear());
                         btn_text = new SimpleDateFormat("yyyy-MM-dd").format(from);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                    if(!active_date_set.remove(btn_text)) {
                        active_date_set.add(btn_text);
                    }
                    activateDates(active_date_set);
                }
            });
        }



        String url = "http://147.47.208.44:9999/api/meetings/my/";
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();


        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_filter :
                FilterSetFragment f = FilterSetFragment.getInstance();
                f.setTargetFragment(WListFragment.this, 1);
                f.show(super.getActivity().getSupportFragmentManager(), FilterSetFragment.TAG_DIALOG_EVENT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) {
            return;
        }
        if( requestCode == 1 ) {
            String url = data.getStringExtra("url");
            ContentValues values = new ContentValues();
            NetworkTask2 networkTask2 = new NetworkTask2(url, values);
            networkTask2.execute();
        }
    }
    public static Intent newIntent(String message) {
        Intent intent = new Intent();
        intent.putExtra("url", message);

        return intent;
    }
    private void activateDates(Set<String> active_dates) {
        int[] btn_date_id_list = {R.id.btn_date_1, R.id.btn_date_2, R.id.btn_date_3, R.id.btn_date_4, R.id.btn_date_5, R.id.btn_date_6, R.id.btn_date_7};
        String url = "http://147.47.208.44:9999/api/meetings/waiting/";
        url += "?";
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("M/d");
        for(int i = 0; i < btn_date_id_list.length; i++) {
            TextView btn_date = (TextView) view.findViewById(btn_date_id_list[i]);
            btn_date.setTextColor(view.getResources().getColor(R.color.colorBlack));
        }
        if(active_dates.size() == 0) {
            for(int i = 0; i < btn_date_id_list.length; i++) {
                TextView btn_date = (TextView) view.findViewById(btn_date_id_list[i]);
                try {
                    Date from = new SimpleDateFormat("M/d").parse(btn_date.getText().toString());
                    Date now = new Date();
                    from.setYear(now.getYear());
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(from);
                    url += "date=" + date + "&";
                } catch(ParseException e){
                    e.printStackTrace();
                }
            }
        }
        else {
            for (String active_date : active_dates) {
                Date from = null;

                try {
                    from = new SimpleDateFormat("yyyy-MM-dd").parse(active_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String to = sdf.format(from);
                for (int i = 0; i < btn_date_id_list.length; i++) {
                    TextView btn_date = (TextView) view.findViewById(btn_date_id_list[i]);
                    if (btn_date.getText().equals(to)) {
                        btn_date.setTextColor(view.getResources().getColor(R.color.colorPoint));
                    }
                }
                url += "date=" + active_date + "&";
            }
        }
        if(active_type_set.size() != 0) {
            for (int active_type : active_type_set) {
                url += "type=" + active_type + "&";
            }
        }
        else {
            for (int i = 1; i <= 3; i++) {
                url += "type=" + i + "&";
            }
        }
        if(active_place_set.size() != 0) {
            for (int active_place : active_place_set) {
                url += "place=" + active_place + "&";
            }
        }
        else{
            for (int i = 1; i <= 3; i++) {
                url += "place=" + i + "&";
            }
        }
        NetworkTask2 networkTask2 = new NetworkTask2(url, values);
        networkTask2.execute();
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

            result = requestHttpURLConnection.request(getContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            active_date_set = new HashSet<>();
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    active_date_set.add(jsonArray.getJSONObject(i).getString("date"));
                }
                activateDates(active_date_set);
                is_initialized = true;
            } catch (JSONException e) {
                e.printStackTrace();
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

            result = requestHttpURLConnection.request(getContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }
        private void removeAllWaitingTeamCard() {
            final LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);
            mRootLinear.removeAllViews();
        }
        private void createWaitingTeamCard(JSONObject json_data) {
            try {
                try {
                    LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);


                    //View v = mRootLinear.inflate(getContext(), R.layout.meeting_team_wlist, mRootLinear);
                    final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View mtw = inflater.inflate(R.layout.meeting_team_wlist, mRootLinear, false);
                    mRootLinear.addView(mtw);
                    LinearLayout top_bg;
                    final RelativeLayout rl_applying;
                    ImageView point_line;
                    TextView description, profile1, profile2, date, place, rating, label;
                    top_bg = (LinearLayout) mtw.findViewById(R.id.top_bg);
                    rl_applying = (RelativeLayout) mtw.findViewById(R.id.rl_applying);
                    label = (TextView) mtw.findViewById(R.id.label);
                    point_line = (ImageView) mtw.findViewById(R.id.point_line);
                    description = (TextView) mtw.findViewById(R.id.description);
                    profile1 = (TextView) mtw.findViewById(R.id.profile1);
                    profile2 = (TextView) mtw.findViewById(R.id.profile2);
                    date = (TextView) mtw.findViewById(R.id.date);
                    place = (TextView) mtw.findViewById(R.id.place);
                    rating = (TextView) mtw.findViewById(R.id.rating);
                    top_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(rl_applying.getVisibility() == View.INVISIBLE) {

                                rl_applying.setVisibility(View.VISIBLE);
                                rl_applying.animate()
                                        .translationY(rl_applying.getHeight())
                                        .alpha(1.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                            }});
                            }
                            else
                                rl_applying.animate()
                                        .translationY(0)
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                rl_applying.setVisibility(View.INVISIBLE);
                                            }});
                        }
                    });
                    String desc_string, profile1_string, profile2_string, date_string, place_string, before_date_string;
                    desc_string = json_data.getString("appeal");
                    profile1_string = json_data.getString("openby_nickname") + " (" +json_data.getString("openby_age") + ")";
                    profile2_string = json_data.getString("openby_belong") + ", "+ json_data.getString("openby_department");
                    before_date_string = json_data.getString("date");
                    try {
                        int label_type = json_data.getInt("meeting_type");
                        switch(label_type){
                            case 1:
                                label.setBackgroundResource(R.drawable.label_2vs2_bg);
                                label.setText("2:2 소개팅");
                                point_line.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                                rating.setTextColor(getResources().getColor(R.color.colorPoint));
                                rl_applying.setBackgroundResource(R.drawable.bottom_rounded_orange);
                                break;
                            case 2:
                                label.setBackgroundResource(R.drawable.label_3vs3_bg);
                                label.setText("3:3 미팅");
                                point_line.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                                rating.setTextColor(getResources().getColor(R.color.color3vs3));
                                rl_applying.setBackgroundResource(R.drawable.bottom_rounded_3vs3);
                                break;
                            case 3:
                                label.setBackgroundResource(R.drawable.label_4vs4_bg);
                                label.setText("4:4 미팅");
                                point_line.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                                rating.setTextColor(getResources().getColor(R.color.color4vs4));
                                rl_applying.setBackgroundResource(R.drawable.bottom_rounded_4vs4);
                                break;
                        }

                        Date before_date = new SimpleDateFormat("yyyy-MM-dd").parse(before_date_string);
                        SimpleDateFormat sdf = new SimpleDateFormat("M월d일");
                        date_string = sdf.format(before_date);
                        place_string = json_data.getString("place_type_name");
                        description.setText(desc_string);
                        profile1.setText(profile1_string);
                        profile2.setText(profile2_string);
                        date.setText(date_string);
                        place.setText(place_string);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final String data = s;
            final LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);
            mRootLinear.animate()
                .setDuration(150)
                .translationX(-100)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        JSONObject jsonObject = null;
                        try {
                            removeAllWaitingTeamCard();
                            if(data == null) return;
                            jsonObject = new JSONObject(data);
                            JSONArray json_results = jsonObject.getJSONArray("results");
                            for(int i = 0; i < json_results.length(); i++) {
                                Log.d("results:", json_results.getJSONObject(i).toString());
                                createWaitingTeamCard(json_results.getJSONObject(i));
                            }
                            mRootLinear.setTranslationX(100);
                            mRootLinear.animate()
                                    .setDuration(150)
                                    .alpha(1.0f)
                                    .translationX(0)
                                    .setListener(null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    }
}
