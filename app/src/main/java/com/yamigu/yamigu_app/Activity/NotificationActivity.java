package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard_Chat;
import com.yamigu.yamigu_app.CustomLayout.WaitingTeamCard;
import com.yamigu.yamigu_app.Etc.Model.NotificationData;
import com.yamigu.yamigu_app.Fragment.HomeFragment;
import com.yamigu.yamigu_app.Fragment.MypageFragment;
import com.yamigu.yamigu_app.Fragment.WListFragment;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NotificationActivity extends AppCompatActivity {
    private Toolbar tb;
    private HashMap<View, Long> view_map;
    private DatabaseReference userDB;
    private  DatabaseReference notiDB;
    private ChildEventListener mChildEventListener;
    private SharedPreferences preferences;
    private String uid;
    private long mLastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_notification);
        tb = (Toolbar) findViewById(R.id.toolbar) ;
        GlobalApplication.notiActivity = this;
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
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        uid = preferences.getString("uid", "");

        //userDB = FirebaseDatabase.getInstance().getReference("user/" + uid);
        //ChildEventListener notiChildEventListenerForNotification = makeChildEventListenerForNotification();

        refresh();
        //loadNotifications(notiChildEventListenerForNotification);
        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        //notiDB.removeEventListener(mChildEventListener);
        GlobalApplication.notiActivity = null;
        super.onDestroy();
    }
    public static List sortByValue(final Map map) {
        List<String> list = new ArrayList();
        list.addAll(map.keySet());
        Collections.sort(list,new Comparator() {
            public int compare(Object o1,Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }
        });
        Collections.reverse(list); // 주석시 오름차순
        return list;
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
    public void refresh() {
        view_map = new HashMap<View, Long>();
        for(Map.Entry<String, NotificationData> noti_data: GlobalApplication.notification_map.entrySet()) {
            createNotification(noti_data.getValue());
        }
        LinearLayout mRootLinear = (LinearLayout) findViewById(R.id.notification_bg);
        mRootLinear.removeAllViews();
        List<View> view_list = sortByValue(view_map);
        for(int i = 0; i < view_list.size(); i++) {
            mRootLinear.addView(view_list.get(i));
        }
    }
    private void createNotification(final NotificationData notificationData) {
        LinearLayout mRootLinear = (LinearLayout) findViewById(R.id.notification_bg);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.notification, mRootLinear, false);

        TextView tv_notification_type = v.findViewById(R.id.tv_notification_type);
        TextView tv_notification_content = v.findViewById(R.id.tv_notification_content);
        TextView tv_notification_time = v.findViewById(R.id.tv_notification_time);
        final long type =  notificationData.type;
        String type_string_array[] = {"", "신청!", "매칭!", "거절!", "대기!", "완료!", "취소!"};
        tv_notification_type.setText(type_string_array[(int)type]);
        tv_notification_content.setText(notificationData.content);

        if(notificationData.isUnread) {
            v.setBackgroundResource(R.drawable.ic_filter_state_pointbg);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    notificationData.isUnread = false;
                    GlobalApplication.notiDB.child(notificationData.id).setValue(notificationData);
                    GlobalApplication.unread_noti_count --;
                    finish();
                }
            });
        }
        else {
            v.setBackgroundResource(R.drawable.ic_filter_state_white);
        }
        final long diff = System.currentTimeMillis() - notificationData.time;
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String str = dayTime.format(new Date(System.currentTimeMillis()));
        String str2 = dayTime.format(new Date(notificationData.time));
        Log.d("now", str);
        Log.d("noti", str2);
        Log.d("diff", ""+diff);
        int minute = (int)(diff / 1000 / 60);

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
        view_map.put(v, new Long(diff));
    }
    private ChildEventListener makeChildEventListenerForNotification() {
        GlobalApplication.unread_noti_count = 0;
        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("DATASNAPSHOT", "" + dataSnapshot.getValue());
                        HashMap mapNotification = (HashMap) dataSnapshot.getValue();
                        boolean isUnread = (boolean) mapNotification.get("isUnread");
                        String id = (String) mapNotification.get("id");
                        long timestamp = (long) mapNotification.get("time");
                        long type = (long) mapNotification.get("type");
                        String content = (String) mapNotification.get("content");
                        NotificationData notificationData = new NotificationData();
                        notificationData.id = id;
                        notificationData.isUnread = isUnread;
                        notificationData.time = timestamp;
                        notificationData.type = type;
                        notificationData.content = content;
                        GlobalApplication.notification_map.put("" + id, notificationData);
                        refresh();
                    }
                } catch(ClassCastException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        return mChildEventListener;
    }
    private void loadNotifications(ChildEventListener mChildEventListener) {
        notiDB.addChildEventListener(mChildEventListener);
    }

}

