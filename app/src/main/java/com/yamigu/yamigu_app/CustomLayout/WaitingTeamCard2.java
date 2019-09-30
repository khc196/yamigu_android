package com.yamigu.yamigu_app.CustomLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;


public class WaitingTeamCard2 extends LinearLayout {

    LinearLayout bg, top_bg;
    RelativeLayout rl_applying;
    ImageView point_line;
    TextView description, profile1, profile2, date, place, rating, label;
    WebView description_w;

    public WaitingTeamCard2(Context context) {
        super(context);
        initView();
    }
    public WaitingTeamCard2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public WaitingTeamCard2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.meeting_team_wlist, this, false);
        addView(v);

        bg = (LinearLayout) findViewById(R.id.bg);
        top_bg = (LinearLayout) findViewById(R.id.top_bg);
        rl_applying = (RelativeLayout) findViewById(R.id.rl_applying);
        label = (TextView) findViewById(R.id.label);
        point_line = (ImageView) findViewById(R.id.point_line);
        //description = (TextView) findViewById(R.id.description);
        description_w = (WebView) findViewById(R.id.description);
        profile1 = (TextView) findViewById(R.id.profile1);
        profile2 = (TextView) findViewById(R.id.profile2);
        date = (TextView) findViewById(R.id.date);
        place = (TextView) findViewById(R.id.place);
        rating = (TextView) findViewById(R.id.rating);

        top_bg.setOnClickListener(new OnClickListener() {
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
        int label_img = typedArray.getInteger(R.styleable.MeetingTeamHome_label, 1);
        switch(label_img){
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


        String desc_string = typedArray.getString(R.styleable.MeetingTeamHome_description);
        desc_string = desc_string.replaceAll("\\u00A0", "");
        //description.setText(desc_string);
        description_w.setBackgroundColor(Color.TRANSPARENT);
        String html = "<html><style type='text/css'>" +
                "@font-face {\" +\n" +
                "font-family: binggrae;" +
                "src: url('font/binggrae.ttf');" +
                "}"+
                "body div {font-family: binggrae;}"+
                "</style>"+
                "<body>"+
                "<div style=" +
                "\"display:table; width:100%; height:100%; wbackground-color:rgba(255,255,255, 0);overflow-y:hidden;\">" +
                "<div style=\"display: table-cell; vertical-align: middle; text-align:center; word-break: break-all; color: black; font-size:14px; padding:3px;overflow-y:hidden;overflow-x:hidden;" +
                "\">"
                +desc_string+
                "</div>" +
                "</div>"+
                "</body>"
                +"</html>";
        description_w.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
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


    public void setLabel(int label_resID) {
        label.setBackgroundResource(label_resID);
    }

    public void setDescription(int description_resID) {
        description.setText(description_resID);
    }
    public void setProfile1(int profile1_resID) {
        profile1.setText(profile1_resID);
    }
    public void setProfile2(int profile2_resID) {
        profile2.setText(profile2_resID);
    }
    public void setDate(int date_resID) {
        date.setText(date_resID);
    }
    public void setPlace(int place_resID) {
        place.setText(place_resID);
    }
}
