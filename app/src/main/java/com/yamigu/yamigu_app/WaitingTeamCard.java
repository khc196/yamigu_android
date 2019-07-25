package com.yamigu.yamigu_app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WaitingTeamCard extends LinearLayout {

    LinearLayout bg;
    ImageView label;
    TextView description, profile1, profile2, date, place;

    public WaitingTeamCard(Context context) {
        super(context);
        initView();
    }
    public WaitingTeamCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public WaitingTeamCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.meeting_team_home, this, false);
        addView(v);

        bg = (LinearLayout) findViewById(R.id.bg);
        label = (ImageView) findViewById(R.id.label);
        description = (TextView) findViewById(R.id.description);
        profile1 = (TextView) findViewById(R.id.profile1);
        profile2 = (TextView) findViewById(R.id.profile2);
        date = (TextView) findViewById(R.id.date);
        place = (TextView) findViewById(R.id.place);
    }
    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MeetingTeamHome);
        setTypeArray(typedArray);
    }
    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MeetingTeamHome, defStyle, 0);
        setTypeArray(typedArray);
    }
    private void setTypeArray(TypedArray typedArray) {
        int label_img = typedArray.getResourceId(R.styleable.MeetingTeamHome_label, R.drawable.label_2vs2);
        label.setImageResource(label_img);

        String desc_string = typedArray.getString(R.styleable.MeetingTeamHome_description);
        description.setText(desc_string);

        String profile1_string = typedArray.getString(R.styleable.MeetingTeamHome_profile1);
        profile1.setText(profile1_string);

        String profile2_string = typedArray.getString(R.styleable.MeetingTeamHome_profile2);
        profile2.setText(profile2_string);

        String date_string = typedArray.getString(R.styleable.MeetingTeamHome_date);
        date.setText(date_string);

        String place_string = typedArray.getString(R.styleable.MeetingTeamHome_place);
        place.setText(place_string);
        typedArray.recycle();
    }


    void setLabel(int label_resID) {
        label.setImageResource(label_resID);
    }

    void setDescription(int description_resID) {
        description.setText(description_resID);
    }
    void setProfile1(int profile1_resID) {
        profile1.setText(profile1_resID);
    }
    void setProfile2(int profile2_resID) {
        profile2.setText(profile2_resID);
    }
    void setDate(int date_resID) {
        date.setText(date_resID);
    }
    void setPlace(int place_resID) {
        place.setText(place_resID);
    }
}
