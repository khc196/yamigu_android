package com.yamigu.yamigu_app;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MeetingApplicationActivity extends AppCompatActivity {
    private Toolbar tb;
    private RelativeLayout selected_type, selected_date, selected_place;
    private TextView selected_type_text, selected_date_text, selected_place_text;
    private ImageButton btn_okay;
    private Button[] btn_select_type_array, btn_select_date_array, btn_select_place_array;
    private ImageView iv_question_type, iv_question_when, iv_question_where, iv_question_appeal;
    private EditText et_appeal;
    private LinearLayout type_view, date_view, place_view, appeal_view;
    private MeetingApplication ma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_application);
        tb = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(tb) ;
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setNavigationIcon(R.drawable.arrow_back_ios);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        selected_type = (RelativeLayout) findViewById(R.id.selected_type);
        selected_date = (RelativeLayout) findViewById(R.id.selected_date);
        selected_place = (RelativeLayout) findViewById(R.id.selected_place);
        selected_type_text = (TextView) findViewById(R.id.selected_type_text);
        selected_date_text = (TextView) findViewById(R.id.selected_date_text);
        selected_place_text = (TextView) findViewById(R.id.selected_place_text);
        btn_select_type_array = new Button[3];
        btn_select_type_array[0] = (Button) findViewById(R.id.btn_select_2vs2);
        btn_select_type_array[1] = (Button) findViewById(R.id.btn_select_3vs3);
        btn_select_type_array[2] = (Button) findViewById(R.id.btn_select_4vs4);

        ma = new MeetingApplication();

        for (final Button button : btn_select_type_array) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (Button obutton : btn_select_type_array) {
                        if(button.getId() != obutton.getId()) {
                            obutton.setBackgroundResource(R.drawable.button_background_non_select);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(button.getId() != ma.getType()) {
                        ma.setType(button.getId());
                        ma.setType_string(button.getText().toString());
                        button.setBackgroundResource(R.drawable.button_background_select);
                        button.setTextColor(Color.WHITE);
                    }
                    else {
                        ma.setType(-1);
                        ma.setType_string("");
                        button.setBackgroundResource(R.drawable.button_background_non_select);
                        button.setTextColor(Color.BLACK);
                    }
                }
            });
        }
        btn_select_date_array = new Button[7];
        btn_select_date_array[0] = (Button) findViewById(R.id.btn_select_date1);
        btn_select_date_array[1] = (Button) findViewById(R.id.btn_select_date2);
        btn_select_date_array[2] = (Button) findViewById(R.id.btn_select_date3);
        btn_select_date_array[3] = (Button) findViewById(R.id.btn_select_date4);
        btn_select_date_array[4] = (Button) findViewById(R.id.btn_select_date5);
        btn_select_date_array[5] = (Button) findViewById(R.id.btn_select_date6);
        btn_select_date_array[6] = (Button) findViewById(R.id.btn_select_date7);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("M월 d일");


        for (final Button button : btn_select_date_array) {
            String getTime = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            button.setText(getTime);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    for (Button obutton : btn_select_date_array) {
                        if(button.getId() != obutton.getId()) {
                            obutton.setBackgroundResource(R.drawable.button_background_non_select);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(button.getId() != ma.getDate()) {
                        ma.setDate(button.getId());
                        ma.setDate_string(button.getText().toString());
                        button.setBackgroundResource(R.drawable.button_background_select);
                        button.setTextColor(Color.WHITE);
                    }
                    else {
                        ma.setDate(-1);
                        ma.setDate_string("");
                        button.setBackgroundResource(R.drawable.button_background_non_select);
                        button.setTextColor(Color.BLACK);
                    }
                }
            });
        }
        btn_select_place_array = new Button[3];
        btn_select_place_array[0] = (Button) findViewById(R.id.btn_select_place1);
        btn_select_place_array[1] = (Button) findViewById(R.id.btn_select_place2);
        btn_select_place_array[2] = (Button) findViewById(R.id.btn_select_place3);
        for (final Button button : btn_select_place_array) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (Button obutton : btn_select_place_array) {
                        if(button.getId() != obutton.getId()) {
                            obutton.setBackgroundResource(R.drawable.button_background_non_select);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(button.getId() != ma.getPlace()) {
                        ma.setPlace(button.getId());
                        ma.setPlace_string(button.getText().toString());
                        button.setBackgroundResource(R.drawable.button_background_select);
                        button.setTextColor(Color.WHITE);
                    }
                    else {
                        ma.setPlace(-1);
                        ma.setPlace_string("");
                        button.setBackgroundResource(R.drawable.button_background_non_select);
                        button.setTextColor(Color.BLACK);
                    }
                }
            });
        }
        type_view = (LinearLayout) findViewById(R.id.type_view);
        date_view = (LinearLayout) findViewById(R.id.date_view);
        place_view = (LinearLayout) findViewById(R.id.place_view);
        appeal_view = (LinearLayout) findViewById(R.id.appeal_view);
        btn_okay = (ImageButton) findViewById(R.id.btn_okay);
        selected_type.setVisibility(View.GONE);
        selected_date.setVisibility(View.GONE);
        selected_place.setVisibility(View.GONE);
        date_view.setVisibility(View.GONE);
        place_view.setVisibility(View.GONE);
        appeal_view.setVisibility(View.GONE);

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ma.isType_selecting()) {
                    if(ma.getType() == -1) {
                        return;
                    }
                    else {
                        ma.setType_selecting(false);
                        ma.setDate_selecting(true);
                        selected_type_text.setText(ma.getType_string());
                        selected_type.setVisibility(View.VISIBLE);
                        type_view.setVisibility(View.GONE);
                        date_view.setVisibility(View.VISIBLE);

                    }
                }
                else if(ma.isDate_selecting()) {
                    if(ma.getDate() == -1) {
                        return;
                    }
                    else {
                        ma.setDate_selecting(false);
                        ma.setPlace_selecting(true);
                        selected_date_text.setText(ma.getDate_string());
                        selected_date.setVisibility(View.VISIBLE);
                        date_view.setVisibility(View.GONE);
                        place_view.setVisibility(View.VISIBLE);
                    }
                }
                else if(ma.isPlace_selecting()) {
                    if(ma.getPlace() == -1) {
                        return;
                    }
                    else {
                        ma.setPlace_selecting(false);
                        ma.setAppeal_selecting(true);
                        selected_place_text.setText(ma.getPlace_string());
                        selected_place.setVisibility(View.VISIBLE);
                        place_view.setVisibility(View.GONE);
                        appeal_view.setVisibility(View.VISIBLE);
                    }
                }
                else if(ma.isAppeal_selecting()) {
                    if(ma.getAppeal().trim().length() == 0) {
                        return;
                    }
                    else {
                        ma.setAppeal_selecting(false);
                    }
                }
            }
        });
    }
    class MeetingApplication {
        private int type, date, place;
        private String appeal;
        private boolean type_selecting, date_selecting, place_selecting, appeal_selecting;
        private String type_string, date_string, place_string;
        MeetingApplication() {
            type = -1;
            date = -1;
            place = -1;
            appeal = "";
            type_selecting = true;
            date_selecting = false;
            place_selecting = false;
            appeal_selecting = false;
            type_string = "";
            date_string = "";
            place_string = "";
        }
        void setType(int type) {
            this.type = type;
        }
        void setDate(int date) {
            this.date = date;
        }
        void setPlace(int place) {
            this.place = place;
        }
        void setAppeal(String appeal) {
            this.appeal = appeal;
        }
        void setType_selecting(boolean type_selecting) {
            this.type_selecting = type_selecting;
        }
        void setDate_selecting(boolean date_selecting) {
            this.date_selecting = date_selecting;
        }
        void setPlace_selecting(boolean place_selecting) {
            this.place_selecting = place_selecting;
        }
        void setAppeal_selecting(boolean appeal_selecting) {
            this.appeal_selecting = appeal_selecting;
        }
        void setType_string(String type_string) {
            this.type_string = type_string;
        }
        void setDate_string(String date_string) {
            this.date_string = date_string;
        }
        void setPlace_string(String place_string){
            this.place_string = place_string;
        }
        int getType() {
            return type;
        }
        int getDate() {
            return date;
        }
        int getPlace() {
            return place;
        }
        String getAppeal() {
            return appeal;
        }
        String getType_string() {
            return type_string;
        }
        String getDate_string() {
            return date_string;
        }
        String getPlace_string() {
            return place_string;
        }
        public boolean isType_selecting() {
            return type_selecting;
        }

        public boolean isDate_selecting() {
            return date_selecting;
        }

        public boolean isPlace_selecting() {
            return place_selecting;
        }

        public boolean isAppeal_selecting() {
            return appeal_selecting;
        }
    }
}
