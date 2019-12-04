package com.yamigu.yamigu_app.CustomLayout;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.Activity.MeetingApplicationActivity;
import com.yamigu.yamigu_app.Activity.RequestListActivity;
import com.yamigu.yamigu_app.Fragment.WListFragment;
import com.yamigu.yamigu_app.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MyMeetingCard extends LinearLayout {

    LinearLayout bg, btn_edit_card, profile_panel;
    ImageView point_line1, point_line2;
    TextView date, label, btn_view_applying, label_matching, text_edit_card, tv_nickname_and_age, tv_belong_and_department;
    private int id, typeInt, place;
    private String date_string, date_string_before;
    private String appeal;
    private String auth_token;
    private SharedPreferences preferences;
    private int main_color;

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
        bg = (LinearLayout) findViewById(R.id.bg);
        label = (TextView) findViewById(R.id.label);
        point_line1 = (ImageView) findViewById(R.id.point_line1);
        point_line2 = (ImageView) findViewById(R.id.point_line2);

        label_matching = (TextView) findViewById(R.id.label_matching);
        btn_edit_card = (LinearLayout) findViewById(R.id.btn_edit_card);
        btn_view_applying = (TextView) findViewById(R.id.btn_view_applying);
        date = (TextView) findViewById(R.id.date);
        profile_panel = (LinearLayout) findViewById(R.id.profile_panel);
        tv_nickname_and_age = (TextView) findViewById(R.id.tv_nickname_and_age);
        tv_belong_and_department = (TextView) findViewById(R.id.tv_belong_and_department);
        btn_view_applying.setPaintFlags(btn_view_applying.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_view_applying.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RequestListActivity.class);
                intent.putExtra("meeting_id", id);

                try {
                    Date date_obj = new SimpleDateFormat("yyyy-MM-dd").parse(date_string_before);
                    SimpleDateFormat sdf = new SimpleDateFormat("M월d일");
                    intent.putExtra("date", sdf.format(date_obj));
                } catch(ParseException e){
                    e.printStackTrace();
                }
                intent.putExtra("type", label.getText().toString());
                String place_array[] = {"신촌/홍대", "건대/왕십리", "강남"};
                intent.putExtra("place", place_array[place-1]);
                getContext().startActivity(intent);
                ((MainActivity)getContext()).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
//        btn_view_waiting.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestFilteredData();
//            }
//        });
        btn_edit_card.setOnClickListener((new OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.dialog = ProgressDialog.show(getContext(), "", "로딩중입니다...", true);
                Intent intent = new Intent(getContext(), MeetingApplicationActivity.class);
                intent.putExtra("meeting_id", id);
                intent.putExtra("type", typeInt);
                intent.putExtra("place", place);
                intent.putExtra("date_string", date.getText().toString());
                intent.putExtra("appeal", appeal);
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
                label.setText("2:2 미팅");
                main_color = getResources().getColor(R.color.colorPoint);
                point_line1.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                point_line2.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                btn_view_applying.setTextColor(getResources().getColor(R.color.colorPoint));
                break;
            case 2:
                label.setText("3:3 미팅");
                main_color = getResources().getColor(R.color.color3vs3);
                point_line1.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                point_line2.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color3vs3));
                break;
            case 3:
                label.setText("3:3 미팅");
                main_color = getResources().getColor(R.color.color4vs4);
                point_line1.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                point_line2.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color4vs4));
                break;
        }

        String month_string = typedArray.getString(R.styleable.MyMeetingCard_month);
        String date_string = typedArray.getString(R.styleable.MyMeetingCard_date);
        date.setText(date_string);
        String dday_string = typedArray.getString(R.styleable.MyMeetingCard_dday);

        int noa_string = typedArray.getInteger(R.styleable.MyMeetingCard_noa, 1);
        typedArray.recycle();
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setType(int type) {
        typeInt = type;
        switch(type){
            case 1:
                label.setText("2:2 미팅");
                main_color = getResources().getColor(R.color.colorPoint);
                point_line1.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                point_line2.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                btn_view_applying.setTextColor(getResources().getColor(R.color.colorPoint));
                break;
            case 2:
                label.setText("3:3 미팅");
                main_color = getResources().getColor(R.color.color3vs3);
                point_line1.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                point_line2.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color3vs3));
                break;
            case 3:
                label.setText("4:4 미팅");
                main_color = getResources().getColor(R.color.color4vs4);
                point_line1.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                point_line2.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                btn_view_applying.setTextColor(getResources().getColor(R.color.color4vs4));
                break;
        }
    }
    public void setLabel(int label_resID) {
        label.setBackgroundResource(label_resID);
    }
    public void setDateString(String date_string) { this.date_string = date_string; }
    public void setDate(String date) {
        this.date_string_before = date;
        try {
            Date date_obj = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 (E)");
            this.date.setText(sdf.format(date_obj));
        } catch(ParseException e) {
            e.printStackTrace();
        }
        //date.setText(Integer.toString(date_integer)+"일");
    }
    public void setPlace(int place) {
        this.place = place;
    }
    public void setNum_of_applying(int num_of_applying_integer) {
        btn_view_applying.setText("신청팀 보기 (" + num_of_applying_integer + ")");
    }
    public void setProfile1(String name, int age) {
        tv_nickname_and_age.setText(name + " (" + age + ")");
    }
    public void setProfile2(String belong, String department) {
        tv_belong_and_department.setText(belong + ", " + department);
}
    public void setMatched(boolean is_matched) {
        if(is_matched) {
            bg.setBackgroundResource(R.drawable.top_rounded_matched);
            label_matching.setText("매칭 완료!");
            label_matching.setTextColor(main_color);
            btn_edit_card.setVisibility(GONE);
            profile_panel.setVisibility(VISIBLE);
            btn_view_applying.setVisibility(GONE);
        }
        else {
            bg.setBackgroundResource(R.drawable.rounded_clean);
            label_matching.setText("매칭 대기중!");
            label_matching.setTextColor(getResources().getColor(R.color.colorBlack));
            btn_edit_card.setVisibility(VISIBLE);
            profile_panel.setVisibility(GONE);
            btn_view_applying.setVisibility(VISIBLE);
        }
    }
}
