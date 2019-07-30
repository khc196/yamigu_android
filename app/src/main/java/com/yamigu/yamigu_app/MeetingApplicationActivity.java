package com.yamigu.yamigu_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MeetingApplicationActivity extends AppCompatActivity {
    private Toolbar tb;
    private Button selected_type, selected_date, selected_place;
    private ImageButton btn_okay;
    private Button[] btn_select_type_array, btn_select_date_array, btn_select_place_array;
    private ImageView iv_question_type, iv_question_when, iv_question_where, iv_question_appeal;
    private EditText et_appeal;
    private LinearLayout type_view, date_view, place_view;
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
        selected_type = (Button) findViewById(R.id.selected_type);
        selected_date = (Button) findViewById(R.id.selected_date);
        selected_place = (Button) findViewById(R.id.selected_place);
        btn_select_type_array = new Button[3];
        btn_select_type_array[0] = (Button) findViewById(R.id.btn_select_2vs2);
        btn_select_type_array[1] = (Button) findViewById(R.id.btn_select_3vs3);
        btn_select_type_array[2] = (Button) findViewById(R.id.btn_select_4vs4);
        btn_select_date_array = new Button[7];
        btn_select_date_array[0] = (Button) findViewById(R.id.btn_select_date1);
        btn_select_date_array[1] = (Button) findViewById(R.id.btn_select_date2);
        btn_select_date_array[2] = (Button) findViewById(R.id.btn_select_date3);
        btn_select_date_array[3] = (Button) findViewById(R.id.btn_select_date4);
        btn_select_date_array[4] = (Button) findViewById(R.id.btn_select_date5);
        btn_select_date_array[5] = (Button) findViewById(R.id.btn_select_date6);
        btn_select_date_array[6] = (Button) findViewById(R.id.btn_select_date7);
        btn_select_place_array = new Button[3];
        btn_select_place_array[0] = (Button) findViewById(R.id.btn_select_place1);
        btn_select_place_array[1] = (Button) findViewById(R.id.btn_select_place2);
        btn_select_place_array[2] = (Button) findViewById(R.id.btn_select_place3);
        type_view = (LinearLayout) findViewById(R.id.type_view);
        date_view = (LinearLayout) findViewById(R.id.date_view);
        place_view = (LinearLayout) findViewById(R.id.place_view);
        btn_okay = (ImageButton) findViewById(R.id.btn_okay);
        selected_type.setVisibility(View.GONE);
        selected_date.setVisibility(View.GONE);
        selected_place.setVisibility(View.GONE);
        date_view.setVisibility(View.GONE);
        place_view.setVisibility(View.GONE);

        ma = new MeetingApplication();

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ma.getType() == -1) {
                    return;
                }
                if(ma.getDate() == -1) {
                    return;
                }
                if(ma.getPlace() == -1) {
                    return;
                }
                if(ma.getAppeal().trim().length() == 0) {
                    return;
                }
            }
        });
    }
    class MeetingApplication {
        private int type, date, place;
        private String appeal;
        MeetingApplication() {
            type = -1;
            date = -1;
            place = -1;
            appeal = "";
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
    }
}
