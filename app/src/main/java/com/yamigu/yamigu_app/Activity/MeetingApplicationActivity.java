package com.yamigu.yamigu_app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yamigu.yamigu_app.CustomLayout.CustomDialog2;
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
    private ImageView iv_question_type, iv_question_when, iv_question_where, iv_question_appeal, type_triangle, date_triangle, place_triangle;
    private EditText et_appeal;
    private LinearLayout type_view, date_view, place_view, appeal_view, ll_for_edit;
    private MeetingApplication ma;
    private TextView tv_max_appeal_length;
    private MeetingApplicationActivity meetingApplicationActivity;
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
    public static ProgressDialog progressDialog = null;
    private CustomDialog2 popupDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_application);
        findViewById(R.id.overall_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch(NullPointerException e) {

                }
                return true;
            }
        });
        meetingApplicationActivity = this;
        progressDialog = ProgressDialog.show(this, "", "로딩중입니다...", true);

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
        type_triangle = findViewById(R.id.type_triangle);
        date_triangle = findViewById(R.id.date_triangle);
        place_triangle = findViewById(R.id.place_triangle);
        type_triangle.setBackgroundResource(R.drawable.triangle_gray);
        date_triangle.setBackgroundResource(R.drawable.triangle_gray);
        place_triangle.setBackgroundResource(R.drawable.triangle_gray);
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
        toast = Toast.makeText(getApplicationContext(), "자신과 친구들을 표현해 주세요!", Toast.LENGTH_SHORT);
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
        appeal_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch(NullPointerException e) {

                }
                return true;
            }
        });
        btn_okay = (Button) findViewById(R.id.btn_okay);
        selected_type_text.setText("인원");
        selected_date_text.setText("날짜");
        selected_place_text.setText("장소");
        date_view.setVisibility(View.GONE);
        place_view.setVisibility(View.GONE);
        appeal_view.setVisibility(View.GONE);
        btn_okay.setVisibility(View.INVISIBLE);
        form_code = initialize_with_prefilled_data(intent);
        if(form_code == EDIT_MEETING || form_code == SEND_REQUEST) {
            selected_type_text.setText(ma.getType_string());
            selected_type.setAlpha(0.5f);
            selected_date_text.setText(ma.getDate_string());
            selected_date.setAlpha(0.5f);
        }
        if(form_code == EDIT_MEETING) {
            selected_place_text.setText(ma.getPlace_string());
            selected_place_text.setTextColor(getResources().getColor(R.color.colorPoint));
            selected_place.setBackgroundResource(R.drawable.bottom_border_orange);
            place_triangle.setBackgroundResource(R.drawable.triangle_orange);
        }
        if(form_code == SEND_REQUEST) {
            selected_place_text.setText(ma.getPlace_string());
            selected_place.setAlpha(0.5f);
        }

        if(form_code == NEW_MEETING) {
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
        if(form_code == EDIT_MEETING) {
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
                    toast.setText("자신과 친구들을 표현해 주세요!");
                    toast.show();
                }
                else if(ma.getAppeal().length() > MAX_APPEAL_LENGTH) {
                    toast.setText("표현을 조금만 줄여주세요!");
                    toast.show();
                }
                else {
                    if(form_code == NEW_MEETING) {
                        progressDialog = ProgressDialog.show(meetingApplicationActivity, "", "미팅 신청중입니다...", true);
                        setDialog("미팅을 신청했어요, 이제 대기팀에서 이성팀을 찾아보세요!\n장소는 달라도 신청가능해요.");
                        String url = "http://106.10.39.154:9999/api/meetings/create/";

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
                        progressDialog = ProgressDialog.show(meetingApplicationActivity, "", "미팅 신청중입니다...", true);
                        setDialog("미팅이 신청되었어요!\n상대방이 수락하면 매칭이 완료됩니다!");
                        String url = "http://106.10.39.154:9999/api/matching/send_request_new/";
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
                                    progressDialog = ProgressDialog.show(meetingApplicationActivity, "", "미팅 수정중입니다...", true);
                                    setDialog("미팅이 수정 되었어요!");
                                    ma.setAppeal(et_appeal.getText().toString());
                                    String url = "http://106.10.39.154:9999/api/meetings/edit/";
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
                                    progressDialog = ProgressDialog.show(meetingApplicationActivity, "", "미팅 삭제중입니다...", true);
                                    setDialog("미팅이 삭제 되었어요!");
                                    String url = "http://106.10.39.154:9999/api/meetings/delete/";
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
                            obutton.setBackgroundResource(0);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(me != ma.getType()) {
                        ma.setType(me);
                        ma.setType_string(button.getText().toString());

                        int colorFrom = getResources().getColor(R.color.colorBackground);
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
                                selected_type_text.setText(button.getText().toString());
                                selected_type_text.setTextColor(getResources().getColor(R.color.colorPoint));
                                selected_type.setBackgroundResource(R.drawable.bottom_border_orange);
                                type_triangle.setBackgroundResource(R.drawable.triangle_orange);
                            }
                        });
                        is_changing = true;
                        colorAnimation.start();
                        colorAnimation2.start();
                    }
                    else {
                        ma.setType(-1);
                        ma.setType_string("");
                        button.setBackgroundResource(0);
                        button.setTextColor(Color.BLACK);
                        selected_type_text.setText("인원");
                        selected_type_text.setTextColor(getResources().getColor(R.color.colorNonselect));
                        selected_type.setBackgroundResource(R.drawable.bottom_border_gray2);
                        type_triangle.setBackgroundResource(R.drawable.triangle_gray);
                    }

                }
            });
        }
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < btn_select_date_array.length; i++) {
            final Button button = btn_select_date_array[i];
            final int me = i+1;

            String getTime = sdf.format(cal.getTime());
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
            String date_text = getTime + " " + DOW[day_of_week];
            button.setText(date_text);
            boolean flag = false;
            for(int j = 0; j < GlobalApplication.active_date_list.size(); j++) {
                try {
                    Date date_obj = transFormat.parse(GlobalApplication.active_date_list.get(j));
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(date_obj);
                    if(cal2.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && cal2.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                        flag = true;
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            cal.add(Calendar.DATE, 1);
            if(ma.getDate_string().equals(date_text)) {
                ma.setDate(me);
                button.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                button.setTextColor(Color.WHITE);
            }
            if(flag) {
                button.setAlpha(0.5f);
                continue;
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
                            obutton.setBackgroundResource(0);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(me != ma.getDate()) {
                        ma.setDate(me);
                        ma.setDate_string(button.getText().toString());
                        int colorFrom = getResources().getColor(R.color.colorBackground);
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
                                selected_date_text.setTextColor(getResources().getColor(R.color.colorPoint));
                                selected_date.setBackgroundResource(R.drawable.bottom_border_orange);
                                date_triangle.setBackgroundResource(R.drawable.triangle_orange);

                            }
                        });
                        is_changing = true;
                        colorAnimation.start();
                        colorAnimation2.start();
                    }
                    else {
                        ma.setDate(-1);
                        ma.setDate_string("");
                        button.setBackgroundResource(0);
                        button.setTextColor(Color.BLACK);
                        selected_date_text.setText("날짜");
                        selected_date_text.setTextColor(getResources().getColor(R.color.colorNonselect));
                        selected_date.setBackgroundResource(R.drawable.bottom_border_gray2);
                        date_triangle.setBackgroundResource(R.drawable.triangle_gray);
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
                            obutton.setBackgroundResource(0);
                            obutton.setTextColor(Color.BLACK);
                        }
                    }
                    if(me != ma.getPlace()) {
                        ma.setPlace(me);
                        ma.setPlace_string(button.getText().toString());
                        int colorFrom = getResources().getColor(R.color.colorBackground);
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
                                selected_place_text.setTextColor(getResources().getColor(R.color.colorPoint));
                                selected_place.setBackgroundResource(R.drawable.bottom_border_orange);
                                place_triangle.setBackgroundResource(R.drawable.triangle_orange);

                            }
                        });
                        is_changing = true;
                        colorAnimation.start();
                        colorAnimation2.start();
                    }
                    else {
                        ma.setPlace(-1);
                        ma.setPlace_string("");
                        button.setBackgroundResource(0);
                        button.setTextColor(Color.BLACK);
                        selected_place_text.setText("장소");
                        selected_place_text.setTextColor(getResources().getColor(R.color.colorNonselect));
                        selected_place.setBackgroundResource(R.drawable.bottom_border_gray2);
                        place_triangle.setBackgroundResource(R.drawable.triangle_gray);
                    }
                }
            });
        }

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
        progressDialog.dismiss();
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

        SimpleDateFormat sdf = new SimpleDateFormat("M월d일");
        try {
            Date date = sdf.parse(dateString);
            dateString = new SimpleDateFormat("M월 d일").format(date);
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
        String placeStringList[] = {"신촌/홍대", "건대/왕십리", "강남"};
        String placeString = placeStringList[placeInt-1];

        switch(typeInt) {
            case 1:
                ma.setType_string("2:2 미팅");
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
    private void setDialog(String text) {
        popupDialog = new CustomDialog2(meetingApplicationActivity);
        popupDialog.setCancelable(true);
        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.text = text;

        popupDialog.getWindow().setGravity(Gravity.CENTER);

        popupDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                final Intent intent = new Intent(meetingApplicationActivity, MainActivity.class);
                MainActivity.selectTab(1);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }
    private void showDialog() {
        popupDialog.show();
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
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            showDialog();
        }
    }
}
