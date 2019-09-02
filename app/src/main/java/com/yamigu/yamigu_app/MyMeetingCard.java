package com.yamigu.yamigu_app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MyMeetingCard extends LinearLayout {

    LinearLayout bg, btn_edit_card;
    ImageView point_line, icon_edit_card;
    TextView place, num_of_applying, month, date, dday, label, btn_view_applying, btn_view_waiting, label_matching_completed, text_edit_card;

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

    void setType(int type) {
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
    void setLabel(int label_resID) {
        label.setBackgroundResource(label_resID);
    }
    void setMonth(int month_integer) { month.setText(Integer.toString(month_integer+1)+"월"); }
    void setDate(int date_integer) { date.setText(Integer.toString(date_integer)+"일"); }
    void setDday(int dday_integer) {
        if(dday_integer == 0) {
            dday.setText("Today");
        }
        else {
            dday.setText("D-"+Integer.toString(dday_integer));
        }
    }
    void setPlace(String place_string) {
        place.setText(place_string);
    }
    void setNum_of_applying(int num_of_applying_integer) {
        num_of_applying.setText(Integer.toString(num_of_applying_integer) + "팀 대기중!");
    }
}
