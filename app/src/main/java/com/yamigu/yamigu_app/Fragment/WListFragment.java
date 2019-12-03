package com.yamigu.yamigu_app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.yamigu.yamigu_app.Activity.GlobalApplication;
import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.Activity.MeetingApplicationActivity;
import com.yamigu.yamigu_app.CustomLayout.CircularImageView;
import com.yamigu.yamigu_app.Etc.ImageUtils;
import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
    public static int meeting_count = 0;
    public static boolean filter_applied = false;
    private LayoutInflater mInflater;
    private View view;
    private Context context;
    static int id = 1;
    private boolean is_initialized = false;
    private SharedPreferences preferences;
    private Menu global_menu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String refresh_url = "";
    private boolean invisible_flag = true;
    public WListFragment() {
        this.active_type_set = new HashSet<>();
        this.active_place_set = new HashSet<>();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // SharedPreferences 수정을 위한 Editor 객체를 얻어옵니다.
        mInflater = inflater;
        view = inflater.inflate(R.layout.fragment_wlist, container, false);
        auth_token = preferences.getString("auth_token", "");
        //tb = (Toolbar) view.findViewById(R.id.toolbar) ;
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                refresh();

            }
        });
        invisible_flag = false;
        final LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("M/d");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        filter_applied = false;
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



//        if (getArguments() != null) {
//            Bundle args = getArguments();
//            filter_applied = true;
//            //activateDates(active_date_set);
//            is_initialized = true;
//        }
//        else {
//            String url = "http://106.10.39.154:9999/api/meetings/my/";
//            ContentValues values = new ContentValues();
//            NetworkTask networkTask = new NetworkTask(url, values);
//            networkTask.execute();
//        }
        active_date_set = new HashSet<>();
        activateDates(active_date_set);
        mRootLinear.setVisibility(View.INVISIBLE);


        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        invisible_flag = true;
        if(!is_initialized) {
            is_initialized = true;
        }
        else {
            if(MainActivity.pager.getCurrentItem() == 1) {
                refresh();
            }
        }
//        ((MainActivity)getActivity()).refresh();
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            final LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);
            mRootLinear.setVisibility(View.INVISIBLE);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        //((MainActivity)getActivity()).refresh();
    }
    public void refresh() {
        String url = refresh_url;
        //MainActivity.dialog = ProgressDialog.show(getContext(), "", "로딩중입니다...", true);

        ContentValues values = new ContentValues();
        NetworkTask2 networkTask2 = new NetworkTask2(url, values);
        networkTask2.execute();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_filter, menu);
        global_menu = menu;
        if(filter_applied)
            global_menu.findItem(R.id.menu_filter).setIcon(R.drawable.icon_filter_applied);
        else {
            global_menu.findItem(R.id.menu_filter).setIcon(R.drawable.icon_filter);
        }
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
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) {
            return;
        }
        if( requestCode == 1 ) {
            if(filter_applied) {
                global_menu.findItem(R.id.menu_filter).setIcon(R.drawable.icon_filter_applied);
            }
            else {
                global_menu.findItem(R.id.menu_filter).setIcon(R.drawable.icon_filter);
            }
            String url = data.getStringExtra("url");
            refresh_url = url;
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
        String url = "http://106.10.39.154:9999/api/meetings/waiting/";
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
        refresh_url = url;
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

            result = requestHttpURLConnection.request(context, url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

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
                    if(!jsonArray.getJSONObject(i).getBoolean("is_matched")) {
                        active_date_set.add(jsonArray.getJSONObject(i).getString("date"));
                    }
                }
                activateDates(active_date_set);
                is_initialized = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(MainActivity.dialog.isShowing()) {
                MainActivity.dialog.dismiss();
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

        private void removeAllWaitingTeamCard() {
            final LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);
            mRootLinear.removeAllViews();
        }
        private void createWaitingTeamCard(final JSONObject json_data) {
            try {
                try {
                    LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);

                    //View v = mRootLinear.inflate(context, R.layout.meeting_team_wlist, mRootLinear);
                    final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View mtw = inflater.inflate(R.layout.meeting_team_wlist, mRootLinear, false);
                    //mRootLinear.addView(mtw);
                    LinearLayout top_bg;
                    final RelativeLayout rl_applying;
                    ImageView point_line;
                    TextView description, profile1, profile2, date, place, label;
                    WebView description_w;
                    top_bg = (LinearLayout) mtw.findViewById(R.id.top_bg);
                    rl_applying = (RelativeLayout) mtw.findViewById(R.id.rl_applying);
                    label = (TextView) mtw.findViewById(R.id.label);
                    point_line = (ImageView) mtw.findViewById(R.id.point_line);
                    //description = (TextView) mtw.findViewById(R.id.description);
                    description_w = (WebView) mtw.findViewById(R.id.description);
                    profile1 = (TextView) mtw.findViewById(R.id.profile1);
                    profile2 = (TextView) mtw.findViewById(R.id.profile2);
                    date = (TextView) mtw.findViewById(R.id.date);
                    place = (TextView) mtw.findViewById(R.id.place);
                    CircularImageView profile_img = (CircularImageView) mtw.findViewById(R.id.iv_profile);

                    String url = json_data.getString("openby_profile");
                    boolean is_matched = json_data.getBoolean("is_matched");
                    mRootLinear.addView(mtw);
                    if(!url.isEmpty()) {
                        mtw.setVisibility(View.INVISIBLE);
                        ContentValues values = new ContentValues();
                        mtw.setTranslationX(100);
                        if(GlobalApplication.bitmap_map.containsKey(url)) {
                            profile_img.setImageBitmap(GlobalApplication.bitmap_map.get(url));
                            mtw.setVisibility(View.VISIBLE);
                            mtw.animate()
                                    .setDuration(50)
                                    .alpha(1.0f)
                                    .translationX(0)
                                    .setListener(null);
                            if(MainActivity.dialog.isShowing()) {
                                MainActivity.dialog.dismiss();
                            }
                        }
                        else {
                            NetworkTask3 networkTask3 = new NetworkTask3(url, values, profile_img, mRootLinear, mtw);
                            networkTask3.execute();
                        }
                    }
                    else {
                        mtw.setVisibility(View.INVISIBLE);
                        mtw.setTranslationX(100);
                        mtw.setVisibility(View.VISIBLE);
                        mtw.animate()
                                .setDuration(50)
                                .alpha(1.0f)
                                .translationX(0)
                                .setListener(null);
                        if(MainActivity.dialog.isShowing()) {
                            MainActivity.dialog.dismiss();
                        }
                    }
                    if(!is_matched) {
                        mtw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (rl_applying.getVisibility() == View.INVISIBLE) {

                                    rl_applying.setVisibility(View.VISIBLE);
                                    rl_applying.animate()
                                            .translationY(rl_applying.getHeight())
                                            .alpha(1.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);
                                                }
                                            });
                                } else
                                    rl_applying.animate()
                                            .translationY(0)
                                            .alpha(0.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);
                                                    rl_applying.setVisibility(View.INVISIBLE);
                                                }
                                            });
                            }
                        });
                        description_w.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                    if (rl_applying.getVisibility() == View.INVISIBLE) {

                                        rl_applying.setVisibility(View.VISIBLE);
                                        rl_applying.animate()
                                                .translationY(rl_applying.getHeight())
                                                .alpha(1.0f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                    }
                                                });
                                    } else
                                        rl_applying.animate()
                                                .translationY(0)
                                                .alpha(0.0f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        rl_applying.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                }
                                return true;
                            }
                        });
                    }
                    final String desc_string, profile1_string, profile2_string, date_string, place_string, before_date_string;
                    desc_string = json_data.getString("appeal");
                    profile1_string = json_data.getString("openby_nickname") + " (" +json_data.getString("openby_age") + ")";
                    profile2_string = json_data.getString("openby_belong") + ", "+ json_data.getString("openby_department");
                    before_date_string = json_data.getString("date");
                    //desc_string = desc_string.replaceAll("", "\u00A0");
                    description_w.setBackgroundColor(Color.TRANSPARENT);
                    String html = "<html><style type='text/css'>" +
                            "@font-face {\" +\n" +
                            "font-family: nanumgothic;" +
                            "src: url('font/nanumgothic.ttf');" +
                            "}"+
                            "body div {font-family: nanumgothic;}"+
                            "</style>"+
                            "<body>"+
                            "<div style=" +
                            "\"display:table; width:100%; height:100%; background-color:rgba(255,255,255, 0);overflow-y:hidden;\">" +
                            "<div style=\"display: table-cell; vertical-align: middle; text-align:center; word-break: break-all; color: black; font-size:14px;overflow-y:hidden;overflow-x:hidden;" +
                            "\">"
                            +desc_string+
                            "</div>" +
                            "</div>"+
                            "</body>"
                            +"</html>";
                    description_w.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
                    if(!is_matched) {
                        try {
                            int label_type = json_data.getInt("meeting_type");
                            switch (label_type) {
                                case 1:
                                    top_bg.setBackgroundResource(R.drawable.top_rounded_with_orange_line);
                                    label.setBackgroundResource(R.drawable.label_2vs2_bg);
                                    place.setBackgroundResource(R.drawable.label_2vs2_bg);
                                    date.setBackgroundResource(R.drawable.label_2vs2_bg);
                                    label.setText("2:2 미팅");
                                    point_line.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                                    rl_applying.setBackgroundResource(R.drawable.bottom_rounded_orange);
                                    break;
                                case 2:
                                    top_bg.setBackgroundResource(R.drawable.top_rounded_3vs3);
                                    label.setBackgroundResource(R.drawable.label_3vs3_bg);
                                    place.setBackgroundResource(R.drawable.label_3vs3_bg);
                                    date.setBackgroundResource(R.drawable.label_3vs3_bg);
                                    label.setText("3:3 미팅");
                                    point_line.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                                    rl_applying.setBackgroundResource(R.drawable.bottom_rounded_3vs3);
                                    break;
                                case 3:
                                    top_bg.setBackgroundResource(R.drawable.top_rounded_4vs4);
                                    label.setBackgroundResource(R.drawable.label_4vs4_bg);
                                    place.setBackgroundResource(R.drawable.label_4vs4_bg);
                                    date.setBackgroundResource(R.drawable.label_4vs4_bg);
                                    label.setText("4:4 미팅");
                                    point_line.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                                    rl_applying.setBackgroundResource(R.drawable.bottom_rounded_4vs4);
                                    break;
                            }

                            Date before_date = new SimpleDateFormat("yyyy-MM-dd").parse(before_date_string);
                            SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 (E)");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("MM월 dd일");
                            date_string = sdf.format(before_date);
                            place_string = json_data.getString("place_type_name");
                            //description.setText(desc_string);
                            profile1.setText(profile1_string);
                            profile2.setText(profile2_string);
                            date.setText(date_string);
                            place.setText(place_string);
                            final String date_string_f = sdf2.format(before_date);
                            final String place_string_f = place_string;
                            rl_applying.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    if(MainActivity.getMyMeetingCount() == 3){
//                                        MainActivity.setDialog("미팅은 일주일에 3번까지만 가능해요!");
//                                        MainActivity.showDialog();
//                                        return;
//                                    }
                                    try {
                                        String url = "http://106.10.39.154:9999/api/matching/send_request/";
                                        ContentValues values = new ContentValues();
                                        values.put("meeting_type", json_data.getInt("meeting_type"));
                                        values.put("date", date_string_f);
                                        values.put("place", json_data.getInt("place_type"));
                                        values.put("place_string", place_string_f);
                                        values.put("meeting_id", json_data.getInt("id"));
                                        rl_applying.setVisibility(View.INVISIBLE);
                                        MainActivity.dialog = ProgressDialog.show(getContext(), "", "미팅 신청중입니다...", true);
                                        NetworkTask4 networkTask4 = new NetworkTask4(url, values);
                                        networkTask4.execute();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            top_bg.setBackgroundResource(R.drawable.top_rounded_matched2);
                            label.setBackgroundResource(R.drawable.label_gray);
                            label.setTextColor(getResources().getColor(R.color.colorNonselect));
                            place.setBackgroundResource(R.drawable.label_gray);
                            place.setTextColor(getResources().getColor(R.color.colorNonselect));
                            date.setBackgroundResource(R.drawable.label_gray);
                            date.setTextColor(getResources().getColor(R.color.colorNonselect));
                            point_line.setBackgroundColor(getResources().getColor(R.color.colorNonselect));
                            Date before_date = new SimpleDateFormat("yyyy-MM-dd").parse(before_date_string);
                            SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 (E)");
                            date_string = sdf.format(before_date);
                            place_string = json_data.getString("place_type_name");
                            //description.setText(desc_string);
                            profile1.setText(profile1_string);
                            profile2.setText(profile2_string);
                            date.setText(date_string);
                            place.setText("매칭완료");
                            final String date_string_f = date_string;
                            final String place_string_f = place_string;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
                .setDuration(50)
                .translationX(-100)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        JSONObject jsonObject = null;
                        try {
                            Log.d("DATA", data);
                            removeAllWaitingTeamCard();
                            if(data == null) return;
                            jsonObject = new JSONObject(data);
                            JSONArray json_results = jsonObject.getJSONArray("results");
                            meeting_count = 0;
                            for(int i = 0; i < json_results.length(); i++) {
                                //Log.d("results:", json_results.getJSONObject(i).toString());
                                createWaitingTeamCard(json_results.getJSONObject(i));
                                if(!json_results.getJSONObject(i).getBoolean("is_matched")) meeting_count++;
                            }
                            if(invisible_flag) {
                                mRootLinear.setVisibility(View.VISIBLE);
                                invisible_flag = false;
                            }
                            mRootLinear.setAlpha(1.0f);
                            mRootLinear.setTranslationX(0);
//                            mRootLinear.setTranslationX(100);
//                            mRootLinear.animate()
//                                    .setDuration(50)
//                                    .alpha(1.0f)
//                                    .translationX(0)
//                                    .setListener(null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    }
    public class NetworkTask3 extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        private CircularImageView civ;
        private LinearLayout rootLayout;
        private View view;
        public NetworkTask3(String url, ContentValues values,  CircularImageView civ, LinearLayout rootLayout, View view) {
            this.url = url;
            this.values = values;
            this.civ = civ;
            this.rootLayout = rootLayout;
            this.view = view;
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
            try {
                while (bm.getWidth() < civ.getWidth()) {
                    bm = Bitmap.createScaledBitmap(bm, bm.getWidth() * 2, bm.getHeight() * 2, false);
                }
                while (bm.getHeight() < civ.getHeight()) {
                    bm = Bitmap.createScaledBitmap(bm, bm.getWidth() * 2, bm.getHeight() * 2, false);
                }
                while(bm.getWidth() < civ.getWidth() && bm.getHeight() < civ.getHeight()) {
                    bm = Bitmap.createScaledBitmap(bm, bm.getWidth() * 2, bm.getHeight() * 2, false);
                }
                if (bm.getWidth() <= bm.getHeight() && bm.getWidth() > civ.getWidth()) {
                    bm = Bitmap.createScaledBitmap(bm, civ.getWidth(), (bm.getHeight() * civ.getWidth()) / bm.getWidth(), false);
                }
                else if (bm.getWidth() >= bm.getHeight() && bm.getHeight() > civ.getHeight()) {
                    bm = Bitmap.createScaledBitmap(bm, (bm.getWidth() * civ.getHeight()) / bm.getHeight(), civ.getHeight(), false);
                }
                if(bm.getWidth() > bm.getHeight()) {
                    bm = ImageUtils.cropCenterBitmap(bm, bm.getHeight(), bm.getHeight());
                }
                else if(bm.getWidth() < bm.getHeight()){
                    bm = ImageUtils.cropCenterBitmap(bm, bm.getWidth(), bm.getWidth());
                }
                Log.d("SIZE", bm.getWidth() + "X" + bm.getHeight() + "    " + civ.getWidth() + "X" + civ.getHeight());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            civ.setImageBitmap(bm);
            GlobalApplication.bitmap_map.put(url, bm);
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .setDuration(50)
                    .alpha(1.0f)
                    .translationX(0)
                    .setListener(null);
            if(MainActivity.dialog.isShowing()) {
                MainActivity.dialog.dismiss();
            }
        }
    }
    public class NetworkTask4 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask4(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(context, url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            if(MainActivity.dialog.isShowing()) {
                MainActivity.dialog.dismiss();
            }
            try {
                jsonObject = new JSONObject(s);
                String message = jsonObject.getString("message");
                Log.d("message", message);

                if(message.equals("created")) {
                    try {
                        Intent intent_me = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent_me);
                    } catch(NullPointerException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getContext(), "미팅이 신청되었어요!", Toast.LENGTH_SHORT).show();
                    MainActivity.setDialog("미팅이 신청되었어요!\n상대방이 수락하면 매칭이 완료됩니다!");
                    MainActivity.showDialog();
                    MainActivity.selectTab(1);
                }
                else if(message.equals("target already matched")) {
                    MainActivity.setDialog("이미 상대방이 매칭되었어요.");
                MainActivity.showDialog();
            }
                else if(message.equals("my card already matched")) {
                MainActivity.setDialog("해당 날짜에 이미 예정된 미팅이 있어요.");
                MainActivity.showDialog();
            }
                else if(message.equals("different type")) {
                    Toast.makeText(getContext(), "해당 날짜에 신청한 미팅과 인원이 달라요!", Toast.LENGTH_SHORT).show();
                }
                else if(message.equals("already exists")) {
                    Toast.makeText(getContext(), "이미 신청했어요!", Toast.LENGTH_SHORT).show();
                }
                else if(message.equals("full card")) {
                    MainActivity.setDialog("미팅은 일주일에 3번까지만 가능해요!");
                    MainActivity.showDialog();
                }
                else if(message.equals("You should create new meeting for matching")) {
                    Intent intent = new Intent(view.getContext(), MeetingApplicationActivity.class);

                    intent.putExtra("type", values.getAsInteger("meeting_type"));
                    intent.putExtra("date_string", values.getAsString("date"));
                    intent.putExtra("place", values.getAsInteger("place"));
                    intent.putExtra("place_string", values.getAsString("place_string"));
                    intent.putExtra("target_id", values.getAsInteger("meeting_id"));
                    startActivity(intent);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
