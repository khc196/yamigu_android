package com.yamigu.yamigu_app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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

    LinearLayout bg;
    ImageView label, point_line, label_matching_completed;
    ImageButton btn_edit_card, btn_view_applying, btn_view_waiting;
    TextView place, num_of_applying, month, date, dday;

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
        label = (ImageView) findViewById(R.id.label);
        point_line = (ImageView) findViewById(R.id.point_line);
        label_matching_completed = (ImageView) findViewById(R.id.label_matching_completed);
        btn_edit_card = (ImageButton) findViewById(R.id.btn_edit_card);
        btn_view_applying = (ImageButton) findViewById(R.id.btn_view_applying);
        btn_view_waiting = (ImageButton) findViewById(R.id.btn_view_waiting);
        place = (TextView) findViewById(R.id.place);
        num_of_applying = (TextView) findViewById(R.id.num_of_applying);
        month = (TextView) findViewById(R.id.month);
        date = (TextView) findViewById(R.id.date);
        dday = (TextView) findViewById(R.id.dday);
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
                label.setImageResource(R.drawable.label2_2vs2);
                //point_line.setBackgroundColor(Color.rgb(255, 132, 36));
                break;
            case 2:
                label.setImageResource(R.drawable.label2_3vs3);
//                point_line.setBackgroundColor(Color.rgb(255, 96, 36));
                break;
            case 3:
                label.setImageResource(R.drawable.label2_4vs4);
//                point_line.setBackgroundColor(Color.rgb(255, 70,0));
                break;
        }

        String month_string = typedArray.getString(R.styleable.MyMeetingCard_month);
        month.setText(month_string);
        String date_string = typedArray.getString(R.styleable.MyMeetingCard_date);
        date.setText(date_string);
        String dday_string = typedArray.getString(R.styleable.MyMeetingCard_dday);
        dday.setText(date_string);

        String place_string = typedArray.getString(R.styleable.MyMeetingCard_place);
        place.setText(place_string);
        int noa_string = typedArray.getInteger(R.styleable.MyMeetingCard_noa, 1);
        num_of_applying.setText(Integer.toString(noa_string) + "팀 대기중!");
        typedArray.recycle();
    }


    void setLabel(int label_resID) {
        label.setImageResource(label_resID);
    }
    void setMonth(int month_resID) { month.setText(month_resID); }
    void setDate(int date_resID) {
        date.setText(date_resID);
    }
    void setDday(int dday_resID) {
        dday.setText(dday_resID);
    }
    void setPlace(int place_resID) {
        place.setText(place_resID);
    }
    void setNum_of_applying(int num_of_applying_resID) { num_of_applying.setText(num_of_applying_resID); }
}
