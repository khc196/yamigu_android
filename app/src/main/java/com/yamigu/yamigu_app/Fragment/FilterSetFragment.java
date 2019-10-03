package com.yamigu.yamigu_app.Fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class FilterSetFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG_DIALOG_EVENT = "dialog_event";
    private Button btn_type_2vs2, btn_type_3vs3, btn_type_4vs4, btn_place_1, btn_place_2, btn_place_3, btn_apply_filter, btn_cancel_filter;
    private ImageView iv_minimum_age, iv_maximum_age, iv_minimum_age_bg_deactivate, iv_maximum_age_bg_deactivate, iv_seekbar_bg;
    private TextView tv_minimum_age, tv_maximum_age;
    private RelativeLayout age_bar_bg;
    private Filter filter;
    private float sx, sy, cx, cy, cx2;
    private int c_move, dpi;
    private boolean is_same_age = false, left_move_flag = false;
    private ViewGroup.MarginLayoutParams params_l, params_r;
    private int l_unit;
    private int seekbar_length;
    private int meeting_count;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static FilterSetFragment getInstance() {
        FilterSetFragment f = new FilterSetFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialogStyle);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        auth_token = preferences.getString("auth_token", "");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_set, container, false);
        // Inflate the layout for this fragment

        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        btn_type_2vs2 = (Button) view.findViewById(R.id.btn_type_2vs2);
        btn_type_3vs3 = (Button) view.findViewById(R.id.btn_type_3vs3);
        btn_type_4vs4 = (Button) view.findViewById(R.id.btn_type_4vs4);
        btn_place_1 = (Button) view.findViewById(R.id.btn_place_1);
        btn_place_2 = (Button) view.findViewById(R.id.btn_place_2);
        btn_place_3 = (Button) view.findViewById(R.id.btn_place_3);
        btn_apply_filter = (Button) view.findViewById(R.id.btn_apply_filter);
        btn_cancel_filter = (Button) view.findViewById(R.id.btn_cancel_filter);
        iv_maximum_age = (ImageView) view.findViewById(R.id.iv_maximum_age);
        iv_minimum_age = (ImageView) view.findViewById(R.id.iv_minimum_age);
        iv_minimum_age_bg_deactivate = (ImageView) view.findViewById(R.id.iv_minimum_age_bg_deactivate);
        iv_maximum_age_bg_deactivate = (ImageView) view.findViewById(R.id.iv_maximum_age_bg_deactivate);
        iv_seekbar_bg = (ImageView) view.findViewById(R.id.seekbar_bg);
        tv_minimum_age = (TextView) view.findViewById(R.id.tv_minimum_age);
        tv_maximum_age = (TextView) view.findViewById(R.id.tv_maximum_age);
        age_bar_bg = (RelativeLayout) view.findViewById(R.id.age_bar_bg);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getDialog().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        dpi = displayMetrics.densityDpi;
        ViewTreeObserver vto = iv_seekbar_bg.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                seekbar_length = iv_seekbar_bg.getWidth();
                l_unit = seekbar_length / 11;
                Log.d("seekbar_length", ""+seekbar_length);
                Log.d("l_unit", ""+l_unit);
                filter.initialize();

            }
        });

        filter = new Filter();



        iv_minimum_age.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float distanceX, distanceY;
                seekbar_length = iv_seekbar_bg.getWidth();

                l_unit = seekbar_length / 11;
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = motionEvent.getX();
                    sy = motionEvent.getY();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    cx = motionEvent.getX();
                    cy = motionEvent.getY();
                    distanceX = cx - sx;
                    distanceY = cy - sy;
                    params_l = (ViewGroup.MarginLayoutParams)iv_minimum_age.getLayoutParams();
                    iv_minimum_age.setLayoutParams(params_l);
                    int movex = params_l.leftMargin + (int)distanceX;
                    if(movex >= 0 && movex / l_unit <= filter.getMaximum_age_current()) {
                        movex = Math.min(movex, filter.getMaximum_age_current() * l_unit);
                        params_l.setMargins(movex, 0, 0, 0);
                        iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(movex, ViewGroup.LayoutParams.MATCH_PARENT));
                        c_move = movex;
                        if(movex / l_unit > 10) {
                            tv_minimum_age.setText("30살+");
                        }
                        else {
                            tv_minimum_age.setText(Integer.toString(20 + movex / l_unit) + "살");
                        }
                        filter.setMinimum_age_current((movex / l_unit));
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int new_move;
                    new_move = (int)(c_move / l_unit) * l_unit;
                    new_move = Math.min(Math.max(0, new_move), seekbar_length);
                    if(new_move / l_unit > 10) {
                        tv_minimum_age.setText("30살+");
                    }
                    else {
                        tv_minimum_age.setText(Integer.toString(20 + new_move / l_unit) + "살");
                    }
                    params_l.setMargins(new_move, 0, 0, 0);
                    iv_minimum_age.setLayoutParams(params_l);
                    iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(new_move, ViewGroup.LayoutParams.MATCH_PARENT));
                    filter.setMinimum_age_current((new_move / l_unit));
                    requestFilteredDataNumber();
                }
                return true;
            }
        });
        iv_maximum_age.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float distanceX, distanceY;
                seekbar_length = iv_seekbar_bg.getWidth();

                l_unit = seekbar_length / 11;
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = motionEvent.getX();
                    sy = motionEvent.getY();
                    is_same_age = filter.getMinimum_age_current() == filter.getMaximum_age_current();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    cx = motionEvent.getX();
                    cy = motionEvent.getY();
                    distanceX = sx - cx;
                    distanceY = sy - cy;
                    params_r = (ViewGroup.MarginLayoutParams)iv_maximum_age.getLayoutParams();
                    Log.d("distanceX", Float.toString(distanceX));
                    if(is_same_age) {
                        if(distanceX >= -1.2 || (distanceX < 0 && filter.getMinimum_age_current() != filter.getMaximum_age_current())) {
                            params_l = (ViewGroup.MarginLayoutParams)iv_minimum_age.getLayoutParams();

                            int movex = params_l.leftMargin - (int)distanceX;
                            if(movex >= 0 && movex / l_unit <= filter.getMaximum_age_current()) {
                                movex = Math.min(movex, filter.getMaximum_age_current() * l_unit);
                                params_l.setMargins(movex, 0, 0, 0);

                                iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(movex, ViewGroup.LayoutParams.MATCH_PARENT));
                                c_move = movex;
                                if(movex / l_unit > 10) {
                                    tv_minimum_age.setText("30살+");
                                }
                                else {
                                    tv_minimum_age.setText(Integer.toString(20 + movex / l_unit) + "살");
                                }
                                filter.setMinimum_age_current((movex / l_unit));
                                iv_minimum_age.setLayoutParams(params_l);
                                sx = cx;
                                if(filter.getMinimum_age_current() != filter.getMaximum_age_current()) {
                                    left_move_flag = true;
                                }
                                else {
                                    left_move_flag = false;
                                }
                            }
                            return true;
                        }
                    }
                    int movex = params_r.rightMargin + (int)distanceX;

                    if(!left_move_flag && movex >= 0 && (11 - (int)(movex / l_unit)) >= filter.getMinimum_age_current()) {
                        movex = Math.min(movex, seekbar_length - filter.getMinimum_age_current() * l_unit);
                        params_r.setMargins(0, 0, movex, 0);
                        iv_maximum_age.setLayoutParams(params_r);
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(movex, ViewGroup.LayoutParams.MATCH_PARENT);
                        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        iv_maximum_age_bg_deactivate.setLayoutParams(params2);
                        c_move = movex;
                        if(movex / l_unit > 0)
                            tv_maximum_age.setText(Integer.toString(31 - movex / l_unit)+"살");
                        else
                            tv_maximum_age.setText("30살+");
                        filter.setMaximum_age_current(11- movex / l_unit);

                        if(c_move >= seekbar_length) {
                            iv_maximum_age.setPadding(0, iv_maximum_age.getPaddingTop(), iv_maximum_age.getPaddingRight(), iv_maximum_age.getPaddingBottom());
                        }
                        else {
                            iv_maximum_age.setPadding(iv_maximum_age.getPaddingTop(), iv_maximum_age.getPaddingTop(), iv_maximum_age.getPaddingRight(), iv_maximum_age.getPaddingBottom());
                        }
                        is_same_age = false;
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int new_move;
                    new_move = (int)(c_move / l_unit) * l_unit;
                    if(left_move_flag && filter.getMaximum_age_current() == filter.getMinimum_age_current()){
                        left_move_flag = false;
                        return true;
                    }
                    if(is_same_age) {
                        iv_minimum_age.setLayoutParams(params_l);
                        new_move = Math.min(Math.max(0, new_move), seekbar_length);
                        tv_minimum_age.setText(Integer.toString(20 + new_move / l_unit)+"살");
                        params_l.setMargins(new_move, 0, 0, 0);
                        iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(new_move, ViewGroup.LayoutParams.MATCH_PARENT));
                        filter.setMinimum_age_current((new_move / l_unit));
                        is_same_age = false;
                        left_move_flag = false;
                        requestFilteredDataNumber();
                        return true;
                    }
                    else {
                        new_move = Math.min(Math.max(0, new_move), seekbar_length);
                        if (new_move / l_unit > 0)
                            tv_maximum_age.setText(Integer.toString(31 - new_move / l_unit) + "살");
                        else
                            tv_maximum_age.setText("30살+");
                        params_r.setMargins(0, 0, new_move, 0);
                        iv_maximum_age.setLayoutParams(params_r);
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(new_move, ViewGroup.LayoutParams.MATCH_PARENT);
                        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        iv_maximum_age_bg_deactivate.setLayoutParams(params2);
                        filter.setMaximum_age_current(11 - new_move / l_unit);
                        requestFilteredDataNumber();
                    }
                }
                return true;
            }
        });
        btn_type_2vs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filter.isSelected_type_2vs2()){
                    filter.setSelected_type_2vs2(true);
                    btn_type_2vs2.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_type_2vs2.setBackgroundResource(R.drawable.rounded_orange_solid);
                }
                else {
                    filter.setSelected_type_2vs2(false);
                    btn_type_2vs2.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_type_2vs2.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
                requestFilteredDataNumber();
            }
        });
        btn_type_3vs3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filter.isSelected_type_3vs3()){
                    filter.setSelected_type_3vs3(true);
                    btn_type_3vs3.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_type_3vs3.setBackgroundResource(R.drawable.rounded_orange_solid);
                }
                else {
                    filter.setSelected_type_3vs3(false);
                    btn_type_3vs3.setTextColor(getResources().getColor(R.color.colorGray));
                    btn_type_3vs3.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
                requestFilteredDataNumber();
            }
        });
        btn_type_4vs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filter.isSelected_type_4vs4()){
                    filter.setSelected_type_4vs4(true);
                    btn_type_4vs4.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_type_4vs4.setBackgroundResource(R.drawable.rounded_orange_solid);
                }
                else {
                    filter.setSelected_type_4vs4(false);
                    btn_type_4vs4.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_type_4vs4.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
                requestFilteredDataNumber();
            }
        });
        btn_place_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filter.isSelected_place_1()){
                    filter.setSelected_place_1(true);
                    btn_place_1.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_place_1.setBackgroundResource(R.drawable.rounded_orange_solid);
                }
                else {
                    filter.setSelected_place_1(false);
                    btn_place_1.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_place_1.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
                requestFilteredDataNumber();
            }
        });
        btn_place_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filter.isSelected_place_2()){
                    filter.setSelected_place_2(true);
                    btn_place_2.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_place_2.setBackgroundResource(R.drawable.rounded_orange_solid);
                }
                else {
                    filter.setSelected_place_2(false);
                    btn_place_2.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_place_2.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
                requestFilteredDataNumber();
            }
        });
        btn_place_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filter.isSelected_place_3()){
                    filter.setSelected_place_3(true);
                    btn_place_3.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_place_3.setBackgroundResource(R.drawable.rounded_orange_solid);
                }
                else {
                    filter.setSelected_place_3(false);
                    btn_place_3.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_place_3.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
                requestFilteredDataNumber();
            }
        });
        btn_apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WListFragment.meeting_count = meeting_count;

                requestFilteredData();
                getDialog().dismiss();
            }
        });
        btn_cancel_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_ui();
                WListFragment.filter_applied = false;
            }
        });



        return view;
    }
    @Override
    public void onClick(View v){

    }
    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.FullScreenDialogStyle);

    }
    private void initialize_ui() {
        btn_type_2vs2.setBackgroundResource(R.drawable.rounded_gray_solid);
        btn_type_2vs2.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_type_3vs3.setBackgroundResource(R.drawable.rounded_gray_solid);
        btn_type_3vs3.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_type_4vs4.setBackgroundResource(R.drawable.rounded_gray_solid);
        btn_type_4vs4.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_place_1.setBackgroundResource(R.drawable.rounded_gray_solid);
        btn_place_1.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_place_2.setBackgroundResource(R.drawable.rounded_gray_solid);
        btn_place_2.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_place_3.setBackgroundResource(R.drawable.rounded_gray_solid);
        btn_place_3.setTextColor(getResources().getColor(R.color.colorNonselect));
        filter.setSelected_type_2vs2(false);
        filter.setSelected_type_3vs3(false);
        filter.setSelected_type_4vs4(false);
        filter.setSelected_place_1(false);
        filter.setSelected_place_2(false);
        filter.setSelected_place_3(false);
        tv_minimum_age.setText("20살");
        params_l = (ViewGroup.MarginLayoutParams)iv_minimum_age.getLayoutParams();
        params_l.setMargins(0, 0, 0, 0);
        iv_minimum_age.setLayoutParams(params_l);
        iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        filter.setMinimum_age_current(0);
        tv_maximum_age.setText("30살+");
        params_r = (ViewGroup.MarginLayoutParams)iv_maximum_age.getLayoutParams();
        params_r.setMargins(0, 0, 0, 0);
        iv_maximum_age.setLayoutParams(params_r);
        iv_maximum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        meeting_count = WListFragment.meeting_count;
        btn_apply_filter.setText(meeting_count + "팀 보기");
        filter.setMaximum_age_current(11);
    }
    private void requestFilteredDataNumber() {
        String url = "http://192.168.43.223:9999/api/meetings/waiting/count/?";
        ContentValues values = new ContentValues();
        List<Integer> selected_types = new LinkedList<>();
        List<Integer> selected_places = new LinkedList<>();
        if(filter.isSelected_type_2vs2())
            selected_types.add(1);
        if(filter.isSelected_type_3vs3())
            selected_types.add(2);
        if(filter.isSelected_type_4vs4())
            selected_types.add(3);
        if(filter.isSelected_place_1())
            selected_places.add(1);
        if(filter.isSelected_place_2())
            selected_places.add(2);
        if(filter.isSelected_place_3())
            selected_places.add(3);
        if(selected_types.size() != 0) {
            for (int i = 0; i < selected_types.size(); i++) {
                url += "type=" + selected_types.get(i) + "&";
            }
        }
        else {
            for (int i = 1; i <= 3; i++) {
                url += "type=" + i + "&";
            }
        }
        if(selected_places.size() != 0) {
            for (int i = 0; i < selected_places.size(); i++) {
                url += "place=" + selected_places.get(i) + "&";
            }
        }
        else {
            for (int i = 1; i <= 3; i++) {
                url += "place=" + i + "&";
            }
        }
        url+="minimum_age="+filter.getMinimum_age_current()+"&";
        url+="maximum_age="+filter.getMaximum_age_current()+"&";
        Set<String> active_dates = WListFragment.active_date_set;
        if(active_dates.size() != 0) {
            for (String active_date : active_dates) {
                url += "date=" + active_date + "&";
            }
        }
        else {
            for(int i = 0; i < 7; i++) {
                url += "date=" + WListFragment.date_list[i] + "&";
            }
        }
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }
    private void requestFilteredData() {
        String url = "http://192.168.43.223:9999/api/meetings/waiting/?";
        ContentValues values = new ContentValues();
        List<Integer> selected_types = new LinkedList<>();
        List<Integer> selected_places = new LinkedList<>();
        Set<Integer> active_place_set;
        Set<Integer> active_type_set;
        WListFragment.filter_applied = false;
        if(filter.isSelected_type_2vs2())
            selected_types.add(1);
        if(filter.isSelected_type_3vs3())
            selected_types.add(2);
        if(filter.isSelected_type_4vs4())
            selected_types.add(3);
        if(filter.isSelected_place_1())
            selected_places.add(1);
        if(filter.isSelected_place_2())
            selected_places.add(2);
        if(filter.isSelected_place_3())
            selected_places.add(3);
        if(selected_types.size() != 0) {
            for (int i = 0; i < selected_types.size(); i++) {
                url += "type=" + selected_types.get(i) + "&";
            }
            WListFragment.filter_applied = true;
        }
        else {
            for (int i = 1; i <= 3; i++) {
                url += "type=" + i + "&";
            }
        }
        if(selected_places.size() != 0) {
            for (int i = 0; i < selected_places.size(); i++) {
                url += "place=" + selected_places.get(i) + "&";
            }
            WListFragment.filter_applied = true;
        }
        else {
            for (int i = 1; i <= 3; i++) {
                url += "place=" + i + "&";
            }
        }
        url+="minimum_age="+filter.getMinimum_age_current()+"&";
        url+="maximum_age="+filter.getMaximum_age_current()+"&";
        Set<String> active_dates = WListFragment.active_date_set;
        if(active_dates.size() != 0) {
            for (String active_date : active_dates) {
                url += "date=" + active_date + "&";
            }
        }
        else {
            for(int i = 0; i < 7; i++) {
                url += "date=" + WListFragment.date_list[i] + "&";
            }
        }

        active_type_set = new HashSet<Integer>(selected_types);
        active_place_set = new HashSet<Integer>(selected_places);
        WListFragment.active_type_set = active_type_set;
        WListFragment.active_place_set = active_place_set;
        WListFragment.minimum_age = filter.getMinimum_age_current();
        WListFragment.maximum_age = filter.getMaximum_age_current();

        Intent intent = WListFragment.newIntent(url);

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

    }
    private class Filter {
        boolean selected_type_2vs2, selected_type_3vs3, selected_type_4vs4, selected_place_1, selected_place_2, selected_place_3;
        int minimum_age_current, maximum_age_current;
        boolean is_initialized;
        public Filter() {
            selected_type_2vs2 = false;
            selected_type_3vs3 = false;
            selected_type_4vs4 = false;
            selected_place_1 = false;
            selected_place_2 = false;
            selected_place_3 = false;
            minimum_age_current = 0;
            maximum_age_current = 11;
            is_initialized = false;
        }
        public void initialize() {
            if(is_initialized) return;
            initialize_ui();
            if(WListFragment.active_type_set.contains(1)) {
                selected_type_2vs2 = true;
                btn_type_2vs2.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_type_2vs2.setBackgroundResource(R.drawable.rounded_orange_solid);
            }
            if(WListFragment.active_type_set.contains(2)) {
                selected_type_3vs3 = true;
                btn_type_3vs3.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_type_3vs3.setBackgroundResource(R.drawable.rounded_orange_solid);
            }
            if(WListFragment.active_type_set.contains(3)) {
                selected_type_4vs4 = true;
                btn_type_4vs4.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_type_4vs4.setBackgroundResource(R.drawable.rounded_orange_solid);
            }
            if(WListFragment.active_place_set.contains(1)) {
                selected_place_1 = true;
                btn_place_1.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_place_1.setBackgroundResource(R.drawable.rounded_orange_solid);
            }
            if(WListFragment.active_place_set.contains(2)) {
                selected_place_2 = true;
                btn_place_2.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_place_2.setBackgroundResource(R.drawable.rounded_orange_solid);
            }
            if(WListFragment.active_place_set.contains(3)) {
                selected_place_3 = true;
                btn_place_3.setTextColor(getResources().getColor(R.color.colorPoint));
                btn_place_3.setBackgroundResource(R.drawable.rounded_orange_solid);
            }
            minimum_age_current = WListFragment.minimum_age;
            maximum_age_current = WListFragment.maximum_age;
            if(minimum_age_current > 10) {
                tv_minimum_age.setText("30살+");
            }
            else {
                tv_minimum_age.setText(Integer.toString(20 + minimum_age_current) + "살");
            }
            params_l = (ViewGroup.MarginLayoutParams)iv_minimum_age.getLayoutParams();
            iv_minimum_age.setLayoutParams(params_l);
            params_l.setMargins(l_unit * minimum_age_current, 0, 0, 0);
            iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(l_unit * minimum_age_current, ViewGroup.LayoutParams.MATCH_PARENT));

            params_r = (ViewGroup.MarginLayoutParams)iv_maximum_age.getLayoutParams();
            params_r.setMargins(0, 0, seekbar_length - maximum_age_current * l_unit, 0);
            iv_maximum_age.setLayoutParams(params_r);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams((11-maximum_age_current) * l_unit, ViewGroup.LayoutParams.MATCH_PARENT);
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            iv_maximum_age_bg_deactivate.setLayoutParams(params2);
            if(maximum_age_current > 10)
                tv_maximum_age.setText("30살+");
            else
                tv_maximum_age.setText(Integer.toString(20 + maximum_age_current)+"살");

            //iv_maximum_age.setPadding(0, iv_maximum_age.getPaddingTop(), iv_maximum_age.getPaddingRight(), iv_maximum_age.getPaddingBottom());
            //iv_maximum_age.setPadding(iv_maximum_age.getPaddingTop(), iv_maximum_age.getPaddingTop(), iv_maximum_age.getPaddingRight(), iv_maximum_age.getPaddingBottom());

            is_initialized = true;
        }
        public void setSelected_type_2vs2(boolean onoff) {
            selected_type_2vs2 = onoff;
        }
        public void setSelected_type_3vs3(boolean onoff) {
            selected_type_3vs3 = onoff;
        }
        public void setSelected_type_4vs4(boolean onoff) {
            selected_type_4vs4 = onoff;
        }
        public void setSelected_place_1(boolean onoff) {
            selected_place_1 = onoff;
        }
        public void setSelected_place_2(boolean onoff) {
            selected_place_2 = onoff;
        }
        public void setSelected_place_3(boolean onoff) {
            selected_place_3 = onoff;
        }
        public void setMinimum_age_current(int current) { minimum_age_current = current; }
        public void setMaximum_age_current(int current) { maximum_age_current = current; }

        public boolean isSelected_type_2vs2() {
            return selected_type_2vs2;
        }

        public boolean isSelected_type_3vs3() {
            return selected_type_3vs3;
        }

        public boolean isSelected_type_4vs4() {
            return selected_type_4vs4;
        }

        public boolean isSelected_place_1() {
            return selected_place_1;
        }

        public boolean isSelected_place_2() {
            return selected_place_2;
        }

        public boolean isSelected_place_3() {
            return selected_place_3;
        }

        public int getMinimum_age_current() {
            return minimum_age_current;
        }

        public int getMaximum_age_current() {
            return maximum_age_current;
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

            result = requestHttpURLConnection.request(getContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d("count: ", s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

                meeting_count = jsonObject.getInt("count");
                Log.d("MEETING_COUNT", ""+meeting_count);
                btn_apply_filter.setText(meeting_count+"팀 보기");
                
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask2(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
//            active_date_set = new HashSet<>();
//            try {
//                jsonArray = new JSONArray(s);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    active_date_set.add(jsonArray.getJSONObject(i).getString("date"));
//                }
//                activateDates(active_date_set);
//                is_initialized = true;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
}
