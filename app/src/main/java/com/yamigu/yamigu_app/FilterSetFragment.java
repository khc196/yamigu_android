package com.yamigu.yamigu_app;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class FilterSetFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG_DIALOG_EVENT = "dialog_event";
    private Button btn_type_2vs2, btn_type_3vs3, btn_type_4vs4, btn_place_1, btn_place_2, btn_place_3;
    private ImageView iv_minimum_age, iv_maximum_age, iv_minimum_age_bg_deactivate, iv_maximum_age_bg_deactivate, iv_seekbar_bg;
    private TextView tv_minimum_age, tv_maximum_age;
    private RelativeLayout age_bar_bg;
    private Filter filter;
    private float sx, sy, cx, cy, cx2;
    private int c_move, dpi;

    private ViewGroup.MarginLayoutParams params;
    public static FilterSetFragment getInstance() {
        FilterSetFragment f = new FilterSetFragment();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
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
        iv_maximum_age = (ImageView) view.findViewById(R.id.iv_maximum_age);
        iv_minimum_age = (ImageView) view.findViewById(R.id.iv_minimum_age);
        iv_minimum_age_bg_deactivate = (ImageView) view.findViewById(R.id.iv_minimum_age_bg_deactivate);
        iv_maximum_age_bg_deactivate = (ImageView) view.findViewById(R.id.iv_maximum_age_bg_deactivate);
        iv_seekbar_bg = (ImageView) view.findViewById(R.id.seekbar_bg);
        tv_minimum_age = (TextView) view.findViewById(R.id.tv_minimum_age);
        tv_maximum_age = (TextView) view.findViewById(R.id.tv_maximum_age);
        age_bar_bg = (RelativeLayout) view.findViewById(R.id.age_bar_bg);
        filter = new Filter();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getDialog().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        dpi = displayMetrics.densityDpi;

        iv_minimum_age.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float distanceX, distanceY;
                int seekbar_length = iv_seekbar_bg.getWidth();
                int l_unit = seekbar_length / 11;
                Log.d("mmmmmmmmmmmmmmmmmmmm", "mmmmmmmmmmmmmmmmmmmmm");
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = motionEvent.getX();
                    sy = motionEvent.getY();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    cx = motionEvent.getX();
                    cy = motionEvent.getY();
                    distanceX = cx - sx;
                    distanceY = cy - sy;
                    params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
                    int movex = params.leftMargin + (int)distanceX;

                    if(movex >= 0 && movex / l_unit <= filter.getMaximum_age_current()) {
                        iv_minimum_age.setLayoutParams(params);
                        movex = Math.min(movex, filter.getMaximum_age_current() * l_unit);
                        params.setMargins(movex, 0, 0, 0);
                        iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(movex, ViewGroup.LayoutParams.MATCH_PARENT));
                        c_move = movex;
                        tv_minimum_age.setText(Integer.toString(20 + movex / l_unit)+"살");
                        filter.setMinimum_age_current((movex / l_unit));
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int new_move;
                    new_move = (int)(c_move / l_unit) * l_unit;
                    new_move = Math.min(Math.max(0, new_move), seekbar_length);
                    tv_minimum_age.setText(Integer.toString(20 + new_move / l_unit)+"살");
                    params.setMargins(new_move, 0, 0, 0);
                    iv_minimum_age.setLayoutParams(params);
                    iv_minimum_age_bg_deactivate.setLayoutParams(new RelativeLayout.LayoutParams(new_move, ViewGroup.LayoutParams.MATCH_PARENT));
                    filter.setMinimum_age_current((new_move / l_unit));
                }
                return true;
            }
        });
        iv_maximum_age.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float distanceX, distanceY;
                int seekbar_length = iv_seekbar_bg.getWidth();
                int l_unit = seekbar_length / 11;
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = motionEvent.getX();
                    sy = motionEvent.getY();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    cx = motionEvent.getX();
                    cy = motionEvent.getY();
                    distanceX = sx - cx;
                    distanceY = sy - cy;
                    params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
                    int movex = params.rightMargin + (int)distanceX;

                    if(movex >= 0 && (11 - (int)(movex / l_unit)) >= filter.getMinimum_age_current()) {
                        movex = Math.min(movex, seekbar_length - filter.getMinimum_age_current() * l_unit);
                        params.setMargins(0, 0, movex, 0);
                        iv_maximum_age.setLayoutParams(params);
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(movex, ViewGroup.LayoutParams.MATCH_PARENT);
                        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        iv_maximum_age_bg_deactivate.setLayoutParams(params2);
                        c_move = movex;
                        if(movex / l_unit > 0)
                            tv_maximum_age.setText(Integer.toString(31 - movex / l_unit)+"살");
                        else
                            tv_maximum_age.setText("30살+");
                        filter.setMaximum_age_current(11- movex / l_unit);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int new_move;
                    new_move = (int)(c_move / l_unit) * l_unit;
                    new_move = Math.min(Math.max(0, new_move), seekbar_length);
                    if(new_move / l_unit > 0)
                        tv_maximum_age.setText(Integer.toString(31 - new_move / l_unit)+"살");
                    else
                        tv_maximum_age.setText("30살+");
                    params.setMargins(0, 0, new_move, 0);
                    iv_maximum_age.setLayoutParams(params);
                    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(new_move, ViewGroup.LayoutParams.MATCH_PARENT);
                    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    iv_maximum_age_bg_deactivate.setLayoutParams(params2);
                    filter.setMaximum_age_current(11 - new_move / l_unit);
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
                    btn_type_2vs2.setTextColor(getResources().getColor(R.color.colorGray));
                    btn_type_2vs2.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
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
                    btn_type_4vs4.setTextColor(getResources().getColor(R.color.colorGray));
                    btn_type_4vs4.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
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
                    btn_place_1.setTextColor(getResources().getColor(R.color.colorGray));
                    btn_place_1.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
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
                    btn_place_2.setTextColor(getResources().getColor(R.color.colorGray));
                    btn_place_2.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
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
                    btn_place_3.setTextColor(getResources().getColor(R.color.colorGray));
                    btn_place_3.setBackgroundResource(R.drawable.rounded_gray_solid);
                }
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
    private class Filter {
        boolean selected_type_2vs2, selected_type_3vs3, selected_type_4vs4, selected_place_1, selected_place_2, selected_place_3;
        int minimum_age_current, maximum_age_current;
        public Filter() {
            selected_type_2vs2 = false;
            selected_type_3vs3 = false;
            selected_type_4vs4 = false;
            selected_place_1 = false;
            selected_place_2 = false;
            selected_place_3 = false;
            minimum_age_current = 0;
            maximum_age_current = 11;
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
}
