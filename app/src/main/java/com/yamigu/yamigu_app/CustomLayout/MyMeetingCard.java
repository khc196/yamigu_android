package com.yamigu.yamigu_app.CustomLayout;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.Activity.MeetingApplicationActivity;
import com.yamigu.yamigu_app.Activity.RequestListActivity;
import com.yamigu.yamigu_app.Activity.TicketOnboardingActivity;
import com.yamigu.yamigu_app.Fragment.WListFragment;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MyMeetingCard extends LinearLayout {

    LinearLayout bg, btn_edit_card;
    RelativeLayout request_panel, profile_panel;
    ImageView point_line, icon_edit_card;
    TextView place, num_of_applying, month, date, dday, label, btn_view_applying, btn_view_waiting, label_matching_completed, text_edit_card, tv_nickname_and_age, tv_belong_and_department;
    private int id, typeInt, placeInt;
    private String date_string;
    private String appeal;
    private String auth_token;
    private SharedPreferences preferences;

    public MyMeetingCard(Context context) {
        super(context);
        initView();
    }
    public MyMeetingCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public MyMeetingCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.my_meeting_card, this, false);
        addView(v);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        auth_token = preferences.getString("auth_token", "");
        request_panel = (RelativeLayout) findViewById(R.id.request_panel);
        profile_panel = (RelativeLayout) findViewById(R.id.profile_panel);
        tv_nickname_and_age = (TextView) findViewById(R.id.tv_nickname_and_age);
        tv_belong_and_department = (TextView) findViewById(R.id.tv_belong_and_department);
        bg = (LinearLayout) findViewById(R.id.bg);
        label = (TextView) findViewById(R.id.label);
        point_line = (ImageView) findViewById(R.id.point_line);
        label_matching_completed = (TextView) findViewById(R.id.label_matching_completed);
        btn_edit_card = (LinearLayout) findViewById(R.id.btn_edit_card);
        icon_edit_card = (ImageView) findViewById(R.id.icon_edit_card);
        text_edit_card = (TextView) findViewById(R.id.text_edit_card);
        btn_view_applying = (TextView) findViewById(R.id.btn_view_applying);
        btn_view_waiting = (TextView) findViewById(R.id.btn_view_waiting);
        place = (TextView) findViewById(R.id.place);
        num_of_applying = (TextView) findViewById(R.id.num_of_applying);
        month = (TextView) findViewById(R.id.month);
        date = (TextView) findViewById(R.id.date);
        dday = (TextView) findViewById(R.id.dday);

        btn_view_applying.setPaintFlags(btn_view_applying.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_view_waiting.setPaintFlags(btn_view_waiting.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_view_applying.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RequestListActivity.class);
                intent.putExtra("meeting_id", id);
                intent.putExtra("date", month.getText().toString() + date.getText().toString());
                intent.putExtra("place", place.getText().toString());
                intent.putExtra("type", label.getText().toString());

                getContext().startActivity(intent);
                ((MainActivity)getContext()).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_view_waiting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFilteredData();
            }
        });
        btn_edit_card.setOnClickListener((new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MeetingApplicationActivity.class);
                intent.putExtra("meeting_id", id);
                intent.putExtra("type", typeInt);
                intent.putExtra("date_string", month.getText().toString() + date.getText().toString());
                intent.putExtra("place_string", place.getText().toString());
                intent.putExtra("appeal", appeal);
                intent.putExtra("place", placeInt);
                getContext().startActivity(intent);
            }
        }));
    }
    public void setAppeal(String appeal) {
        this.appeal = appeal;
    }
    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyMeetingCard);
        setTypeArray(typedArray);
    }
    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyMeetingCard, defStyle, 0);
        setTypeArray(typedArray);
    }
    private void setTypeArray(TypedArray typedArray) {
        int label_img = typedArray.getInteger(R.styleable.MyMeetingCard_label, 1);
        switch(label_img){
            case 1:
                label.setBackgroundResource(R.drawable.label_2vs2_bg);
                label.setText("2:2 소개팅");
                point_line.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                month.setBackgroundResource(R.drawable.cal_month_2vs2_bg);
                btn_view_waiting.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_view_applying.setTextColor(getResources().getColor(R.color.colorPoint));
                icon_edit_card.setBackgroundResource(R.drawable.icon_edit_2vs2);
                text_edit_card.setTextColor(getResources().getColor(R.color.colorPoint));
                break;
            case 2:
                label.setBackgroundResource(R.drawable.label_3vs3_bg);
                label.setText("3:3 미팅");
                point_line.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                month.setBackgroundResource(R.drawable.cal_month_3vs3_bg);
                btn_view_waiting.setTextColor(getResources().getColor(R.color.color3vs3));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color3vs3));
                icon_edit_card.setBackgroundResource(R.drawable.icon_edit_3vs3);
                text_edit_card.setTextColor(getResources().getColor(R.color.color3vs3));
                break;
            case 3:
                label.setBackgroundResource(R.drawable.label_4vs4_bg);
                label.setText("3:3 미팅");
                point_line.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                month.setBackgroundResource(R.drawable.cal_month_3vs3_bg);
                btn_view_waiting.setTextColor(getResources().getColor(R.color.color4vs4));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color4vs4));
                icon_edit_card.setBackgroundResource(R.drawable.icon_edit_4vs4);
                text_edit_card.setTextColor(getResources().getColor(R.color.color4vs4));
                break;
        }

        String month_string = typedArray.getString(R.styleable.MyMeetingCard_month);
        month.setText(month_string);
        String date_string = typedArray.getString(R.styleable.MyMeetingCard_date);
        date.setText(date_string);
        String dday_string = typedArray.getString(R.styleable.MyMeetingCard_dday);
        dday.setText(dday_string);

        String place_string = typedArray.getString(R.styleable.MyMeetingCard_place);
        place.setText(place_string);
        int noa_string = typedArray.getInteger(R.styleable.MyMeetingCard_noa, 1);
        num_of_applying.setText(Integer.toString(noa_string) + "팀 신청!");
        typedArray.recycle();
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setType(int type) {
        typeInt = type;
        switch(type){
            case 1:
                label.setBackgroundResource(R.drawable.label_2vs2_bg);
                label.setText("2:2 소개팅");
                point_line.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                month.setBackgroundResource(R.drawable.cal_month_2vs2_bg);
                btn_view_waiting.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_view_applying.setTextColor(getResources().getColor(R.color.colorPoint));
                icon_edit_card.setBackgroundResource(R.drawable.icon_edit_2vs2);
                text_edit_card.setTextColor(getResources().getColor(R.color.colorPoint));
                break;
            case 2:
                label.setBackgroundResource(R.drawable.label_3vs3_bg);
                label.setText("3:3 미팅");
                point_line.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                month.setBackgroundResource(R.drawable.cal_month_3vs3_bg);
                btn_view_waiting.setTextColor(getResources().getColor(R.color.color3vs3));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color3vs3));
                icon_edit_card.setBackgroundResource(R.drawable.icon_edit_3vs3);
                text_edit_card.setTextColor(getResources().getColor(R.color.color3vs3));
                break;
            case 3:
                label.setBackgroundResource(R.drawable.label_4vs4_bg);
                label.setText("4:4 미팅");
                point_line.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                month.setBackgroundResource(R.drawable.cal_month_4vs4_bg);
                btn_view_waiting.setTextColor(getResources().getColor(R.color.color4vs4));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color4vs4));
                icon_edit_card.setBackgroundResource(R.drawable.icon_edit_4vs4);
                text_edit_card.setTextColor(getResources().getColor(R.color.color4vs4));
                break;
        }
    }
    public void setLabel(int label_resID) {
        label.setBackgroundResource(label_resID);
    }
    public void setDateString(String date_string) { this.date_string = date_string; }
    public void setMonth(int month_integer) { month.setText(Integer.toString(month_integer+1)+"월"); }
    public void setDate(int date_integer) { date.setText(Integer.toString(date_integer)+"일"); }
    public void setDday(int dday_integer) {
        if(dday_integer == 0) {
            dday.setText("Today");
        }
        else {
            dday.setText("D-"+Integer.toString(dday_integer));
        }
    }
    public void setPlace(int place) {
        placeInt = place;
    }
    public void setPlaceString(String place_string) {
        place.setText(place_string);
    }
    public void setNum_of_applying(int num_of_applying_integer) {
        num_of_applying.setText(Integer.toString(num_of_applying_integer) + "팀 신청!");
    }
    public void setProfile1(String name, int age) {
        tv_nickname_and_age.setText(name + " (" + age + ")");
    }
    public void setProfile2(String belong, String department) {
        tv_belong_and_department.setText(belong + ", " + department);
    }
    public void setMatched(boolean is_matched) {
        if(is_matched) {
            label_matching_completed.setVisibility(VISIBLE);
            btn_view_waiting.setVisibility(INVISIBLE);
            btn_edit_card.setVisibility(INVISIBLE);
            profile_panel.setVisibility(VISIBLE);
            request_panel.setVisibility(INVISIBLE);
        }
        else {
            label_matching_completed.setVisibility(INVISIBLE);
            btn_view_waiting.setVisibility(VISIBLE);
            btn_edit_card.setVisibility(VISIBLE);
            profile_panel.setVisibility(INVISIBLE);
            request_panel.setVisibility(VISIBLE);
        }
    }
    private void requestFilteredData() {
        String url = "http://192.168.43.223:9999/api/meetings/waiting/?";
        ContentValues values = new ContentValues();
        List<Integer> selected_types = new LinkedList<>();
        List<Integer> selected_places = new LinkedList<>();
        Set<Integer> active_place_set;
        Set<Integer> active_type_set;
        selected_types.add(typeInt);
        selected_places.add(placeInt);

        if(selected_types.size() != 0) {
            for (int i = 0; i < selected_types.size(); i++) {
                url += "type=" + selected_types.get(i) + "&";
            }
        }
        else {
            for (int i = 1; i <= 3; i++) {
                url += "type=" + i + "&";
            }
        }
        if(selected_places.size() != 0) {
            for (int i = 0; i < selected_places.size(); i++) {
                url += "place=" + selected_places.get(i) + "&";
            }
        }
        else {
            for (int i = 1; i <= 3; i++) {
                url += "place=" + i + "&";
            }
        }
        url+="minimum_age="+0+"&";
        url+="maximum_age="+11+"&";
        Set<String> active_date_set = new HashSet<String>();

        active_date_set.add(date_string);

        url += "date=" + date_string + "&";


        active_type_set = new HashSet<Integer>(selected_types);
        active_place_set = new HashSet<Integer>(selected_places);


        Bundle args = new Bundle();

        WListFragment wListFragment = new WListFragment();

        wListFragment.active_type_set = active_type_set;
        wListFragment.active_place_set = active_place_set;
        wListFragment.active_date_set = active_date_set;
        wListFragment.minimum_age = 0;
        wListFragment.maximum_age = 11;

        args.putString("url", url);
        wListFragment.setArguments(args);
        ((MainActivity)getContext()).loadFragment(wListFragment);
        ((MainActivity)getContext()).nav_home.setImageResource(R.drawable.nav_home);
        ((MainActivity)getContext()).nav_wlist.setImageResource(R.drawable.nav_wlist_selected);
        ((MainActivity)getContext()).nav_mypage.setImageResource(R.drawable.nav_mypage);
        ((MainActivity)getContext()).nav_more.setImageResource(R.drawable.nav_more);
    }
}
