package com.yamigu.yamigu_app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MeetingApplicationActivity extends AppCompatActivity {
    private Toolbar tb;
    private RelativeLayout selected_type, selected_date, selected_place;
    private TextView selected_type_text, selected_date_text, selected_place_text;
    private Button btn_okay, btn_edit, btn_delete;
    private Button[] btn_select_type_array, btn_select_date_array, btn_select_place_array;
    private ImageView iv_question_type, iv_question_when, iv_question_where, iv_question_appeal;
    private EditText et_appeal;
    private LinearLayout type_view, date_view, place_view, appeal_view, ll_for_edit;
    private MeetingApplication ma;
    private TextView tv_max_appeal_length;
    Toast toast;
    private final int MAX_APPEAL_LENGTH = 100;
    private final String[] DOW = {"", "일", "월", "화", "수", "목", "금", "토"};
    private String auth_token;
    private int form_code;
    private int target_id, edit_id;
    private boolean is_changing;
    private SharedPreferences preferences;
    private final int NEW_MEETING = 0;
    private final int SEND_REQUEST = 1;
    private final int EDIT_MEETING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_application);

        Intent intent = getIntent();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        auth_token = preferences.getString("auth_token", "");

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
        ll_for_edit = (LinearLayout) findViewById(R.id.ll_for_edit);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_select_type_array = new Button[3];
        btn_select_type_array[0] = (Button) findViewById(R.id.btn_select_2vs2);
        btn_select_type_array[1] = (Button) findViewById(R.id.btn_select_3vs3);
        btn_select_type_array[2] = (Button) findViewById(R.id.btn_select_4vs4);
        et_appeal = (EditText) findViewById(R.id.edittext_appeal);
        tv_max_appeal_length = (TextView) findViewById(R.id.max_appeal_length);
        ll_for_edit.setVisibility(View.INVISIBLE);
        tv_max_appeal_length.setText("0 / "+Integer.toString(MAX_APPEAL_LENGTH));
        tv_max_appeal_length.setVisibility(View.INVISIBLE);
        ma = new MeetingApplication();
        toast = Toast.makeText(getApplicationContext(), "뭐라도 써주세요!", Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        int toastbackgroundColor = ResourcesCompat.getColor(toastView.getResources(), R.color.colorPoint, null);
        toastView.getBackground().setColorFilter(toastbackgroundColor, PorterDuff.Mode.SRC_IN);
        TextView toasttv = (TextView) toastView.findViewById(android.R.id.message);
        toasttv.setTextColor(Color.WHITE);
        form_code = 0;
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


        btn_select_place_array = new Button[3];
        btn_select_place_array[0] = (Button) findViewById(R.id.btn_select_place1);
        btn_select_place_array[1] = (Button) findViewById(R.id.btn_select_place2);
        btn_select_place_array[2] = (Button) findViewById(R.id.btn_select_place3);

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
        form_code = initialize_with_prefilled_data(intent);
        if(form_code == NEW_MEETING || form_code == EDIT_MEETING) {
            selected_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ma.reselect(ma.RESELECT_TYPE);

                    type_view.setVisibility(View.VISIBLE);
                    date_view.setVisibility(View.GONE);
                    place_view.setVisibility(View.GONE);
                    appeal_view.setVisibility(View.GONE);
                    tv_max_appeal_length.setVisibility(View.INVISIBLE);
                    btn_okay.setVisibility(View.INVISIBLE);
                    tv_max_appeal_length.setVisibility(View.INVISIBLE);
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
                    tv_max_appeal_length.setVisibility(View.INVISIBLE);
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
                    tv_max_appeal_length.setVisibility(View.INVISIBLE);
                }
            });
        }
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    if(form_code == NEW_MEETING) {
                        String url = "http://147.47.208.44:9999/api/meetings/create/";

                        ContentValues values = new ContentValues();
                        String new_date = ma.getDate_string().substring(0, ma.getDate_string().length() - 1);
                        values.put("meeting_type", ma.getType());
                        values.put("date", new_date.trim());
                        values.put("place", ma.getPlace());
                        values.put("appeal", ma.getAppeal());

                        NetworkTask networkTask = new NetworkTask(url, values);
                        networkTask.execute();
                    }
                    else if(form_code == SEND_REQUEST){
                        String url = "http://147.47.208.44:9999/api/matching/send_request_new/";
                        ContentValues values = new ContentValues();
                        String new_date = ma.getDate_string().substring(0, ma.getDate_string().length() - 1);
                        values.put("meeting_type", ma.getType());
                        values.put("date", new_date.trim());
                        values.put("place", ma.getPlace());
                        values.put("appeal", ma.getAppeal());
                        values.put("meeting_id", target_id);
                        NetworkTask networkTask = new NetworkTask(url, values);
                        networkTask.execute();
                    }
                }
            }
        });
        if(form_code == EDIT_MEETING) {

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(view.getContext());
                    alert_confirm.setMessage("수정 사항을 저장하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ma.setAppeal(et_appeal.getText().toString());
                                    String url = "http://147.47.208.44:9999/api/meetings/edit/";
                                    ContentValues values = new ContentValues();
                                    String new_date = ma.getDate_string().substring(0, ma.getDate_string().length() - 1);
                                    values.put("meeting_type", ma.getType());
                                    values.put("date", new_date.trim());
                                    values.put("place", ma.getPlace());
                                    values.put("appeal", ma.getAppeal());
                                    values.put("meeting_id", edit_id);
                                    NetworkTask networkTask = new NetworkTask(url, values);
                                    networkTask.execute();
                                }
                            }).setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(view.getContext());
                    alert_confirm.setMessage("미팅을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String url = "http://147.47.208.44:9999/api/meetings/delete/";
                                    ContentValues values = new ContentValues();
                                    values.put("meeting_id", edit_id);
                                    NetworkTask networkTask = new NetworkTask(url, values);
                                    networkTask.execute();
                                }
                            }).setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();

                }
            });
        }
        for (int i = 0; i < btn_select_type_array.length; i++) {
            final Button button = btn_select_type_array[i];
            final int me = i+1;
            if(ma.getType() == me) {
                button.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                button.setTextColor(Color.WHITE);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(is_changing)
                        return;
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

                        int colorFrom = getResources().getColor(R.color.colorPrimary);
                        int colorTo = getResources().getColor(R.color.colorPoint);
                        int colorFrom2 = getResources().getColor(R.color.colorBlack);
                        int colorTo2 = getResources().getColor(R.color.colorPrimary);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom2, colorTo2);
                        colorAnimation.setDuration(350); // milliseconds
                        colorAnimation2.setDuration(350);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                button.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                        colorAnimation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                is_changing = false;
                                if (ma.getDate() == -1) {
                                    ma.reselect(ma.RESELECT_DATE);
                                    type_view.setVisibility(View.GONE);
                                    date_view.setVisibility(View.VISIBLE);
                                    place_view.setVisibility(View.GONE);
                                } else if (ma.getPlace() == -1) {
                                    ma.reselect(ma.RESELECT_PLACE);
                                    type_view.setVisibility(View.GONE);
                                    date_view.setVisibility(View.GONE);
                                    place_view.setVisibility(View.VISIBLE);
                                } else {
                                    ma.reselect(ma.RESELECT_APPEAL);
                                    type_view.setVisibility(View.GONE);
                                    date_view.setVisibility(View.GONE);
                                    place_view.setVisibility(View.GONE);
                                    appeal_view.setVisibility(View.VISIBLE);
                                    tv_max_appeal_length.setVisibility(View.VISIBLE);
                                    if(form_code == NEW_MEETING)
                                        btn_okay.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                button.setTextColor((int) animator.getAnimatedValue());
                                selected_type_text.setText(ma.getType_string());
                            }
                        });
                        is_changing = true;
                        colorAnimation.start();
                        colorAnimation2.start();
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
        for (int i = 0; i < btn_select_date_array.length; i++) {
            final Button button = btn_select_date_array[i];
            final int me = i+1;

            String getTime = sdf.format(cal.getTime());
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DATE, 1);
            String date_text = getTime + " " + DOW[day_of_week];
            button.setText(date_text);
            if(ma.getDate_string().equals(date_text)) {
                ma.setDate(me);
                button.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                button.setTextColor(Color.WHITE);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    if(is_changing) {
                        return;
                    }
                    Button obutton;
                    for (int j = 0; j < btn_select_date_array.length; j++) {
                        obutton = btn_select_date_array[j];
                        if(me != j+1) {
                            obutton.setBackgroundResource(R.drawable.button_background_non_select);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(me != ma.getDate()) {
                        ma.setDate(me);
                        ma.setDate_string(button.getText().toString());
                        int colorFrom = getResources().getColor(R.color.colorPrimary);
                        int colorTo = getResources().getColor(R.color.colorPoint);
                        int colorFrom2 = getResources().getColor(R.color.colorBlack);
                        int colorTo2 = getResources().getColor(R.color.colorPrimary);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom2, colorTo2);
                        colorAnimation.setDuration(350); // milliseconds
                        colorAnimation2.setDuration(350);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                button.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                        colorAnimation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                is_changing = false;
                                if(ma.getType() == -1) {
                                    ma.reselect(ma.RESELECT_TYPE);
                                    type_view.setVisibility(View.VISIBLE);
                                    date_view.setVisibility(View.GONE);
                                    place_view.setVisibility(View.GONE);
                                    tv_max_appeal_length.setVisibility(View.INVISIBLE);
                                }
                                else if(ma.getPlace() == -1) {
                                    ma.reselect(ma.RESELECT_PLACE);
                                    type_view.setVisibility(View.GONE);
                                    date_view.setVisibility(View.GONE);
                                    place_view.setVisibility(View.VISIBLE);
                                    tv_max_appeal_length.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    ma.reselect(ma.RESELECT_APPEAL);
                                    type_view.setVisibility(View.GONE);
                                    date_view.setVisibility(View.GONE);
                                    place_view.setVisibility(View.GONE);
                                    appeal_view.setVisibility(View.VISIBLE);
                                    tv_max_appeal_length.setVisibility(View.VISIBLE);

                                    if(form_code == NEW_MEETING)
                                        btn_okay.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                button.setTextColor((int) animator.getAnimatedValue());
                                selected_date_text.setText(ma.getDate_string());

                            }
                        });
                        is_changing = true;
                        colorAnimation.start();
                        colorAnimation2.start();
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
        for (int i = 0; i < btn_select_place_array.length; i++) {
            final Button button = btn_select_place_array[i];
            final int me = i+1;
            if(ma.getPlace() == me) {
                button.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                button.setTextColor(Color.WHITE);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(is_changing)
                        return;
                    Button obutton;
                    for(int j = 0; j < btn_select_place_array.length; j++) {
                        obutton = btn_select_place_array[j];
                        if(me != j+1) {
                            obutton.setBackgroundResource(R.drawable.button_background_non_select);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(me != ma.getPlace()) {
                        ma.setPlace(me);
                        ma.setPlace_string(button.getText().toString());
                        int colorFrom = getResources().getColor(R.color.colorPrimary);
                        int colorTo = getResources().getColor(R.color.colorPoint);
                        int colorFrom2 = getResources().getColor(R.color.colorBlack);
                        int colorTo2 = getResources().getColor(R.color.colorPrimary);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom2, colorTo2);
                        colorAnimation.setDuration(350); // milliseconds
                        colorAnimation2.setDuration(350);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                button.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                        colorAnimation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                is_changing = false;
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
                                    tv_max_appeal_length.setVisibility(View.VISIBLE);
                                    if(form_code == NEW_MEETING)
                                        btn_okay.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                button.setTextColor((int) animator.getAnimatedValue());
                                selected_place_text.setText(ma.getPlace_string());

                            }
                        });
                        is_changing = true;
                        colorAnimation.start();
                        colorAnimation2.start();
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

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = ((GlobalApplication)getApplicationContext()).getCurrentActivity();
        if (this.equals(currActivity))
            ((GlobalApplication)getApplicationContext()).setCurrentActivity(null);

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
    private int initialize_with_prefilled_data(Intent intent) {
        try {
            if (intent.getExtras().getInt("type") <= 0) {
                return NEW_MEETING;
            }
        }
        catch (NullPointerException e){
            return NEW_MEETING;
        }
        int typeInt = intent.getExtras().getInt("type");
        int placeInt = intent.getExtras().getInt("place");
        String dateString = intent.getExtras().getString("date_string");
        String appealString = intent.getExtras().getString("appeal");
        target_id = intent.getExtras().getInt("target_id");
        edit_id = intent.getExtras().getInt("meeting_id");

        SimpleDateFormat sdf = new SimpleDateFormat("m월d일");
        try {
            Date date = sdf.parse(dateString);
            dateString = new SimpleDateFormat("m월 d일").format(date);
            Calendar cal = Calendar.getInstance();
            Date today = new Date();
            cal.setTime(today);

            for(int i = 0; i < 7; i++) {
                if(date.getDate() == cal.getTime().getDate()) {
                    break;
                }
                cal.add(Calendar.DATE, 1);
            }
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
            dateString += " " + DOW[day_of_week];

        } catch (ParseException e){
            e.printStackTrace();
        }
        String placeString = intent.getExtras().getString("place_string");

        switch(typeInt) {
            case 1:
                ma.setType_string("2:2 소개팅");
                break;
            case 2:
                ma.setType_string("3:3 미팅");
                break;
            case 3:
                ma.setType_string("4:4 미팅");
                break;
        }
        ma.setType(typeInt);
        ma.setDate_string(dateString);
        ma.setPlace(placeInt);
        ma.setPlace_string(placeString);
        ma.setAppeal(appealString);
        selected_type_text.setText(ma.getType_string());
        selected_date_text.setText(ma.getDate_string());
        selected_place_text.setText(ma.getPlace_string());

        if(target_id == 0) {
            ma.reselect(ma.RESELECT_TYPE);
            type_view.setVisibility(View.GONE);
            date_view.setVisibility(View.GONE);
            place_view.setVisibility(View.GONE);
            appeal_view.setVisibility(View.VISIBLE);
            tv_max_appeal_length.setVisibility(View.VISIBLE);
            btn_okay.setVisibility(View.INVISIBLE);
            ll_for_edit.setVisibility(View.VISIBLE);
            et_appeal.setText(ma.getAppeal());
            return EDIT_MEETING;
        }
        else {
            type_view.setVisibility(View.GONE);
            date_view.setVisibility(View.GONE);
            place_view.setVisibility(View.GONE);
            appeal_view.setVisibility(View.VISIBLE);
            tv_max_appeal_length.setVisibility(View.VISIBLE);
            btn_okay.setVisibility(View.VISIBLE);
            return SEND_REQUEST;
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

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }
}
