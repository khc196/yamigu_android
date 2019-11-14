package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.CustomLayout.WaitingTeamCard;
import com.yamigu.yamigu_app.Etc.Model.NotificationData;
import com.yamigu.yamigu_app.Fragment.MypageFragment;
import com.yamigu.yamigu_app.Fragment.WListFragment;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    private Toolbar tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
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
        for(Map.Entry<String, NotificationData> noti_data: GlobalApplication.notification_map.entrySet()) {
            createNotification(noti_data.getValue());
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
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
    private void createNotification(final NotificationData notificationData) {
        LinearLayout mRootLinear = (LinearLayout) findViewById(R.id.notification_bg);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.notice, mRootLinear, true);
        TextView tv_notification_type = v.findViewById(R.id.tv_notification_type);
        TextView tv_notification_content = v.findViewById(R.id.tv_notification_content);
        TextView tv_notification_time = v.findViewById(R.id.tv_notification_time);
        final int type =  notificationData.type;
        String type_string_array[] = {"", "신청!", "매칭!", "거절!", "대기!", "완료!", "취소!"};
        tv_notification_type.setText(type_string_array[type]);
        tv_notification_content.setText(notificationData.content);
        final long diff = System.currentTimeMillis() - notificationData.time;
        int minute = (int)(diff / 1000 * 60);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(notificationData.data);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        final JSONObject intentData = jsonObject;
        if(notificationData.isUread) {
            mRootLinear.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    switch(type) {
                        case 1:
                        case 3:
                            try {
                                intent = new Intent(getApplicationContext(), RequestListActivity.class);
                                int meeting_id = intentData.getInt("meeting_id");
                                String month = intentData.getString("month");
                                String date = intentData.getString("date");
                                String place = intentData.getString("place");
                                String type = intentData.getString("type");
                                intent.putExtra("meeting_id", meeting_id);
                                intent.putExtra("date", month + date);
                                intent.putExtra("place", place);
                                intent.putExtra("type", type);
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                        case 6:
                            finish();
                            break;
                        case 4:
                            MainActivity.me.selectTab(2);
                            MainActivity.me.loadFragment(new WListFragment());
                            finish();
                            break;
                        case 5:
                            MainActivity.me.selectTab(3);
                            MainActivity.me.loadFragment(new MypageFragment());
                            finish();
                            break;
                        default:
                            break;
                    }
                    if(intent != null) {
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        else {
            mRootLinear.setBackgroundColor(getResources().getColor(R.color.colorPointBG));
        }

        tv_notification_time.setText("지금");
        int hour = 0;
        int day = 0;
        int week = 0;
        int month = 0;
        if(minute > 0) {
            String minute_text = minute + "분 전";
            tv_notification_time.setText(minute_text);
        }
        if(minute > 60) {
            hour = minute / 60;
            String hour_text = hour + "시간 전";
            tv_notification_time.setText(hour_text);
        }
        if(hour > 24) {
            day = hour / 24;
            String day_text = day + "일 전";
            tv_notification_time.setText(day_text);
        }
        if(day > 7) {
            week = day / 7;
            String week_text = week + "주 전";
            tv_notification_time.setText(week_text);
        }
        if(week > 4) {
            month = week / 4;
            String month_text = month + "달 전";
            tv_notification_time.setText(month_text);
        }
    }
}

