package com.yamigu.yamigu_app.CustomLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class TodayScheduledTeamCard extends LinearLayout {

    LinearLayout bg;
    TextView place, man, woman, label;

    public TodayScheduledTeamCard(Context context) {
        super(context);
        initView();
    }
    public TodayScheduledTeamCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public TodayScheduledTeamCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.today_scheduled_team_home, this, false);
        addView(v);

        bg = (LinearLayout) findViewById(R.id.bg);
        label = (TextView) findViewById(R.id.label);
        place = (TextView) findViewById(R.id.place);
        man = (TextView) findViewById(R.id.man);
        woman = (TextView) findViewById(R.id.woman);
    }
    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScheduledTeamHome);
        setTypeArray(typedArray);
    }
    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScheduledTeamHome, defStyle, 0);
        setTypeArray(typedArray);
    }
    private void setTypeArray(TypedArray typedArray) {
        int label_img = typedArray.getResourceId(R.styleable.ScheduledTeamHome_label, R.drawable.label_2vs2_bg);
        label.setBackgroundResource(label_img);

        String place_string = typedArray.getString(R.styleable.ScheduledTeamHome_place);
        place.setText(place_string);

        String man_string = typedArray.getString(R.styleable.ScheduledTeamHome_man);
        man.setText(man_string);

        String woman_string = typedArray.getString(R.styleable.ScheduledTeamHome_woman);
        woman.setText(woman_string);

        typedArray.recycle();
    }


    public void setLabel(int label_resID) {
        label.setBackgroundResource(label_resID);
    }

    public void setPlace(int description_resID) { place.setText(description_resID);
    }
    public void setMan(int profile1_resID) { man.setText(profile1_resID);
    }
    public void setWoman(int profile2_resID) {
        woman.setText(profile2_resID);
    }
}
