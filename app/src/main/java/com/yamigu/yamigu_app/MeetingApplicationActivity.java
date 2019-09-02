package com.yamigu.yamigu_app;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MeetingApplicationActivity extends AppCompatActivity {
    private Toolbar tb;
    private RelativeLayout selected_type, selected_date, selected_place;
    private TextView selected_type_text, selected_date_text, selected_place_text;
    private Button btn_okay;
    private Button[] btn_select_type_array, btn_select_date_array, btn_select_place_array;
    private ImageView iv_question_type, iv_question_when, iv_question_where, iv_question_appeal;
    private EditText et_appeal;
    private LinearLayout type_view, date_view, place_view, appeal_view;
    private MeetingApplication ma;
    private TextView tv_max_appeal_length;
    Toast toast;
    private final int MAX_APPEAL_LENGTH = 50;
    private final String[] DOW = {"", "일", "월", "화", "수", "목", "금", "토"};
    private String auth_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_application);

        Intent intent = getIntent();
        auth_token = intent.getExtras().getString("auth_token");

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
        et_appeal = (EditText) findViewById(R.id.edittext_appeal);
        tv_max_appeal_length = (TextView) findViewById(R.id.max_appeal_length);
        tv_max_appeal_length.setText("0 / "+Integer.toString(MAX_APPEAL_LENGTH));
        ma = new MeetingApplication();
        toast = Toast.makeText(getApplicationContext(), "뭐라도 써주세요!", Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        int toastbackgroundColor = ResourcesCompat.getColor(toastView.getResources(), R.color.colorPoint, null);
        toastView.getBackground().setColorFilter(toastbackgroundColor, PorterDuff.Mode.SRC_IN);
        TextView toasttv = (TextView) toastView.findViewById(android.R.id.message);
        toasttv.setTextColor(Color.WHITE);
        et_appeal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv_max_appeal_length.setText(Integer.toString(et_appeal.getText().length()) + " / " + Integer.toString(MAX_APPEAL_LENGTH));
            }
        });

        for (int i = 0; i < btn_select_type_array.length; i++) {
            final Button button = btn_select_type_array[i];
            final Button obutton;
            final int me = i+1;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button obutton;
                    for (int j = 0; j < btn_select_type_array.length; j++){
                        obutton = btn_select_type_array[j];
                        if(me != j+1) {
                            obutton.setBackgroundResource(R.drawable.button_background_non_select);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(me != ma.getType()) {
                        ma.setType(me);
                        ma.setType_string(button.getText().toString());
                        button.setBackgroundResource(R.drawable.button_background_select);
                        button.setTextColor(Color.WHITE);
                        selected_type_text.setText(ma.getType_string());
                        if(ma.getDate() == -1) {
                            ma.reselect(ma.RESELECT_DATE);
                            type_view.setVisibility(View.GONE);
                            date_view.setVisibility(View.VISIBLE);
                            place_view.setVisibility(View.GONE);
                        }
                        else if(ma.getPlace() == -1) {
                            ma.reselect(ma.RESELECT_PLACE);
                            type_view.setVisibility(View.GONE);
                            date_view.setVisibility(View.GONE);
                            place_view.setVisibility(View.VISIBLE);
                        }
                        else {
                            ma.reselect(ma.RESELECT_APPEAL);
                            type_view.setVisibility(View.GONE);
                            date_view.setVisibility(View.GONE);
                            place_view.setVisibility(View.GONE);
                            appeal_view.setVisibility(View.VISIBLE);
                            btn_okay.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        ma.setType(-1);
                        ma.setType_string("");
                        button.setBackgroundResource(R.drawable.button_background_non_select);
                        button.setTextColor(Color.BLACK);
                        selected_type_text.setText("인원");
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
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DATE, 1);
            button.setText(getTime + " " + DOW[day_of_week]);
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
                        selected_date_text.setText(ma.getDate_string());
                        if(ma.getType() == -1) {
                            ma.reselect(ma.RESELECT_TYPE);
                            type_view.setVisibility(View.VISIBLE);
                            date_view.setVisibility(View.GONE);
                            place_view.setVisibility(View.GONE);
                        }
                        else if(ma.getPlace() == -1) {
                            ma.reselect(ma.RESELECT_PLACE);
                            type_view.setVisibility(View.GONE);
                            date_view.setVisibility(View.GONE);
                            place_view.setVisibility(View.VISIBLE);
                        }
                        else {
                            ma.reselect(ma.RESELECT_APPEAL);
                            type_view.setVisibility(View.GONE);
                            date_view.setVisibility(View.GONE);
                            place_view.setVisibility(View.GONE);
                            appeal_view.setVisibility(View.VISIBLE);
                            btn_okay.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        ma.setDate(-1);
                        ma.setDate_string("");
                        button.setBackgroundResource(R.drawable.button_background_non_select);
                        button.setTextColor(Color.BLACK);
                        selected_date_text.setText("날짜");
                    }

                }
            });
        }
        btn_select_place_array = new Button[3];
        btn_select_place_array[0] = (Button) findViewById(R.id.btn_select_place1);
        btn_select_place_array[1] = (Button) findViewById(R.id.btn_select_place2);
        btn_select_place_array[2] = (Button) findViewById(R.id.btn_select_place3);
        for (int i = 0; i < btn_select_place_array.length; i++) {
            final Button button = btn_select_place_array[i];
            final int me = i+1;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button obutton;
                    for(int j = 0; j < btn_select_place_array.length; j++) {
                        obutton = btn_select_type_array[j];
                        if(me != j+1) {
                            obutton.setBackgroundResource(R.drawable.button_background_non_select);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(button.getId() != ma.getPlace()) {
                        ma.setPlace(me);
                        ma.setPlace_string(button.getText().toString());
                        button.setBackgroundResource(R.drawable.button_background_select);
                        button.setTextColor(Color.WHITE);
                        selected_place_text.setText(ma.getPlace_string());
                        if(ma.getType() == -1) {
                            ma.reselect(ma.RESELECT_TYPE);
                            type_view.setVisibility(View.VISIBLE);
                            date_view.setVisibility(View.GONE);
                            place_view.setVisibility(View.GONE);
                        }
                        else if(ma.getDate() == -1) {
                            ma.reselect(ma.RESELECT_DATE);
                            type_view.setVisibility(View.GONE);
                            date_view.setVisibility(View.VISIBLE);
                            place_view.setVisibility(View.GONE);
                        }
                        else {
                            ma.reselect(ma.RESELECT_APPEAL);
                            type_view.setVisibility(View.GONE);
                            date_view.setVisibility(View.GONE);
                            place_view.setVisibility(View.GONE);
                            appeal_view.setVisibility(View.VISIBLE);
                            btn_okay.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        ma.setPlace(-1);
                        ma.setPlace_string("");
                        button.setBackgroundResource(R.drawable.button_background_non_select);
                        button.setTextColor(Color.BLACK);
                        selected_place_text.setText("장소");
                    }
                }
            });
        }
        type_view = (LinearLayout) findViewById(R.id.type_view);
        date_view = (LinearLayout) findViewById(R.id.date_view);
        place_view = (LinearLayout) findViewById(R.id.place_view);
        appeal_view = (LinearLayout) findViewById(R.id.appeal_view);
        btn_okay = (Button) findViewById(R.id.btn_okay);
        selected_type_text.setText("인원");
        selected_date_text.setText("날짜");
        selected_place_text.setText("장소");
        date_view.setVisibility(View.GONE);
        place_view.setVisibility(View.GONE);
        appeal_view.setVisibility(View.GONE);
        btn_okay.setVisibility(View.INVISIBLE);

        selected_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ma.reselect(ma.RESELECT_TYPE);

                type_view.setVisibility(View.VISIBLE);
                date_view.setVisibility(View.GONE);
                place_view.setVisibility(View.GONE);
                appeal_view.setVisibility(View.GONE);
                btn_okay.setVisibility(View.INVISIBLE);
            }
        });
        selected_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ma.reselect(ma.RESELECT_DATE);

                type_view.setVisibility(View.GONE);
                date_view.setVisibility(View.VISIBLE);
                place_view.setVisibility(View.GONE);
                appeal_view.setVisibility(View.GONE);
                btn_okay.setVisibility(View.INVISIBLE);
            }
        });
        selected_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ma.reselect(ma.RESELECT_PLACE);

                type_view.setVisibility(View.GONE);
                date_view.setVisibility(View.GONE);
                place_view.setVisibility(View.VISIBLE);
                appeal_view.setVisibility(View.GONE);
                btn_okay.setVisibility(View.INVISIBLE);
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ma.isAppeal_selecting()) {
                    ma.setAppeal(et_appeal.getText().toString());
                    if(ma.getAppeal().trim().isEmpty()) {
                        toast.setText("뭐라도 써주세요!");
                        toast.show();
                    }
                    else if(ma.getAppeal().length() > MAX_APPEAL_LENGTH) {
                        toast.setText(Integer.toString(MAX_APPEAL_LENGTH) + "자를 넘기면 안돼요~");
                        toast.show();
                    }
                    else {
                        String url = "http://192.168.0.10:9999/api/meetings/create/";
                        ContentValues values = new ContentValues();
                        String new_date = ma.getDate_string().substring(0, ma.getDate_string().length()-1);
                        Log.d("MeetingApplication", "meeting_type: "+ma.getType());
                        Log.d("MeetingApplication", "date: "+new_date.trim());
                        Log.d("MeetingApplication", "place: "+ma.getPlace());
                        values.put("meeting_type", ma.getType());

                        values.put("date", new_date.trim());
                        values.put("place", ma.getPlace());
                        values.put("appeal", ma.getAppeal());

                        NetworkTask networkTask = new NetworkTask(url, values);
                        networkTask.execute();
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    class MeetingApplication {
        private int type, date, place;
        private String appeal;
        private boolean type_selecting, date_selecting, place_selecting, appeal_selecting;
        private String type_string, date_string, place_string;
        public final int RESELECT_TYPE = 1, RESELECT_DATE = 2, RESELECT_PLACE = 3, RESELECT_APPEAL = 4;
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
        public void reselect(int reselect_target) {
            switch(reselect_target) {
                case RESELECT_TYPE:
                    type_selecting = true;
                    date_selecting = false;
                    place_selecting = false;
                    appeal_selecting = false;
                    break;
                case RESELECT_DATE:
                    type_selecting = false;
                    date_selecting = true;
                    place_selecting = false;
                    appeal_selecting = false;
                    break;
                case RESELECT_PLACE:
                    type_selecting = false;
                    date_selecting = false;
                    place_selecting = true;
                    appeal_selecting = false;
                    break;
                case RESELECT_APPEAL:
                    type_selecting = false;
                    date_selecting = false;
                    place_selecting = false;
                    appeal_selecting = true;
            }
        }
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

            result = requestHttpURLConnection.request(url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            finish();
        }
    }
}
