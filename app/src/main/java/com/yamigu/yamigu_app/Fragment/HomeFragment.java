package com.yamigu.yamigu_app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yamigu.yamigu_app.Activity.ChattingActivity;
import com.yamigu.yamigu_app.Activity.GlobalApplication;
import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.Activity.MeetingApplicationActivity;
import com.yamigu.yamigu_app.Activity.NotificationActivity;
import com.yamigu.yamigu_app.Adapter.FragmentAdapter;
import com.yamigu.yamigu_app.CustomLayout.CircularImageView;
import com.yamigu.yamigu_app.CustomLayout.CustomViewPager;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard_Chat;
import com.yamigu.yamigu_app.Etc.ImageUtils;
import com.yamigu.yamigu_app.Etc.Model.ChatData;
import com.yamigu.yamigu_app.Etc.Model.NotificationData;
import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.Activity.TicketOnboardingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private Toolbar tb;
    private String auth_token, uid, nickname;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Context context;
    private MyMeetingCardFrame myMeetingCardFrame;
    private RelativeLayout btn_go_yamigu;
    private LinearLayout ll_root_pane;
    private DatabaseReference userDB, managerDB;
    public static DatabaseReference notiDB;
    private Fragment me;
    private TextView tv_unread_noti_count, tv_ticket_count, tv_recommendation;
    //private ViewPager pager;
    private CustomViewPager pager;
    public static FragmentAdapter fragmentAdapter;
    private TabLayout tabIndicator;
    private int total_num;
    public static int ticket_count = 0;
    public static int ACTION_START_CHAT = 1;
    private long mLastClickTime = 0;
    View view;
    private class MyMeetingCardFrame {
        private MyMeetingCard mmc_list[];
        private MyMeetingCard_Chat mmc_c_list[];
        private int active_length;
        public MyMeetingCardFrame(View view) {
            mmc_list = new MyMeetingCard[3];
            mmc_c_list = new MyMeetingCard_Chat[3];
            mmc_list[0] = (MyMeetingCard) view.findViewById(R.id.mmc_1);
            mmc_list[1] = (MyMeetingCard) view.findViewById(R.id.mmc_2);
            mmc_list[2] = (MyMeetingCard) view.findViewById(R.id.mmc_3);

            mmc_c_list[0] = (MyMeetingCard_Chat) view.findViewById(R.id.mmc_c_1);
            mmc_c_list[1] = (MyMeetingCard_Chat) view.findViewById(R.id.mmc_c_2);
            mmc_c_list[2] = (MyMeetingCard_Chat) view.findViewById(R.id.mmc_c_3);

            for(int i = 0; i < mmc_list.length; i++) {
                mmc_list[i].setVisibility(View.GONE);
            }
            for(int i = 0; i < mmc_c_list.length; i++) {
                mmc_c_list[i].setVisibility(View.GONE);
            }
        }

        public int getActive_length() {
            return active_length;
        }
        public void setActive_length(int length) {
            active_length = length;
        }
        public void initialize() {

        }
        public MyMeetingCard[] getMmc_list() {
            return mmc_list;
        }

        public MyMeetingCard_Chat[] getMmc_c_list() {
            return mmc_c_list;
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GlobalApplication.current_chatting_room = 0;
        me = this;
        GlobalApplication.homefragment = (HomeFragment)me;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        view = inflater.inflate(R.layout.fragment_home, container, false);
        auth_token = preferences.getString("auth_token", "");
        uid = preferences.getString("uid", "");
        nickname = preferences.getString("nickname", "");
        ticket_count = preferences.getInt("num_of_ticket", 0);
        userDB = FirebaseDatabase.getInstance().getReference("user/" + uid);
        ll_root_pane = view.findViewById(R.id.root_pane);
        ll_root_pane.setVisibility(View.INVISIBLE);
//        tb = (Toolbar) view.findViewById(R.id.toolbar_h) ;
//        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        btn_go_yamigu = (RelativeLayout) view.findViewById(R.id.btn_go_yamigu);
        btn_go_yamigu.setVisibility(View.GONE);
        btn_go_yamigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MeetingApplicationActivity.class);
                startActivity(intent);
            }
        });
        tv_unread_noti_count = view.findViewById(R.id.unread_noti_count);
        tv_unread_noti_count.setVisibility(View.INVISIBLE);
        tv_unread_noti_count.setText("0");
        tv_ticket_count = view.findViewById(R.id.ticket_count);
        tv_ticket_count.setText(Integer.toString(ticket_count));
        tv_recommendation = view.findViewById(R.id.tv_recommendation);
        tv_recommendation.setText(nickname+"님을 위한 추천 미팅");
//        String url = "http://106.10.39.154:9999/api/meetings/my/";
//        ContentValues values = new ContentValues();
//        NetworkTask networkTask = new NetworkTask(url, values);
//        networkTask.execute();

        String url2 = "http://106.10.39.154:9999/api/meetings/my_past/";
        ContentValues values2 = new ContentValues();
        NetworkTask2 networkTask2 = new NetworkTask2(url2, values2);
        networkTask2.execute();

        pager = view.findViewById(R.id.pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //refresh();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setAdapter(fragmentAdapter);
        tabIndicator = view.findViewById(R.id.tab_indicator);
        tabIndicator.setupWithViewPager(pager);

        String url5 = "http://106.10.39.154:9999/api/meetings/recommendation/";
        ContentValues values5 = new ContentValues();
        NetworkTask5 networkTask5 = new NetworkTask5(url5, values5);
        networkTask5.execute();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
//        ChildEventListener notiChildEventListenerForNotification = makeChildEventListenerForNotification();
//        notiDB = loadNotifications(notiChildEventListenerForNotification);
        ll_root_pane.setVisibility(View.INVISIBLE);
        refresh();
        //((MainActivity)getActivity()).refresh();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
        this.context = context;
    }
    public void refresh() {
        myMeetingCardFrame = new MyMeetingCardFrame(view);
        tv_ticket_count.setText(Integer.toString(ticket_count));

        tv_unread_noti_count.setText(Integer.toString(GlobalApplication.unread_noti_count));
        if(GlobalApplication.unread_noti_count == 0) {
            tv_unread_noti_count.setVisibility(View.INVISIBLE);
        }
        else {
            tv_unread_noti_count.setVisibility(View.VISIBLE);
        }
        String url = "http://106.10.39.154:9999/api/meetings/my/";
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
        if(fragmentAdapter != null) {
            fragmentAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_items, menu);
        //RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.menu_ticket).getActionView();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_ticket:
                intent = new Intent(context, TicketOnboardingActivity.class);
                startActivity(intent);
                ((MainActivity)context).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                break;
            case R.id.menu_notification:
                intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
                ((MainActivity)context).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                break;
        }
        return true;
    }
    private DatabaseReference loadMessages(int matching_id, ChildEventListener mChildEventListener) {
        DatabaseReference receivedChatReference = userDB.child("receivedMessages").child(""+matching_id);

        receivedChatReference.addChildEventListener(mChildEventListener);
        return receivedChatReference;
    }
    private DatabaseReference loadNotifications(ChildEventListener mChildEventListener) {
        DatabaseReference receivedNotificationReference = userDB.child("notifications");

        receivedNotificationReference.addChildEventListener(mChildEventListener);
        return receivedNotificationReference;
    }
//    private ChildEventListener makeChildEventListener(final MyMeetingCard_Chat myMeetingCard_chat, final int matching_id) {
//        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("message/" + matching_id);
//        ChildEventListener mChildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if(dataSnapshot.getValue() != null) {
//                    HashMap mapMessage = (HashMap) dataSnapshot.getValue();
//                    boolean isUnread = (boolean) mapMessage.get("isUnread");
//                    String id = (String) mapMessage.get("id");
//                    if(isUnread) {
//                        try {
//                            int unreadCount = 0;
//                            myMeetingCard_chat.unread_count.setVisibility(View.VISIBLE);
//                            if(GlobalApplication.unread_chat_map.containsKey(matching_id)) {
//                                unreadCount = GlobalApplication.unread_chat_map.get(matching_id);
//                                unreadCount++;
//                                myMeetingCard_chat.unread_count.setText(Integer.toString(unreadCount));
//                                GlobalApplication.unread_chat_map.put(matching_id, unreadCount);
//                                ((MainActivity)getActivity()).refresh();
//                            }
//                            else {
//                                unreadCount = 0;
//                                unreadCount++;
//                                myMeetingCard_chat.unread_count.setText(Integer.toString(unreadCount));
//                                GlobalApplication.unread_chat_map.put(matching_id, unreadCount);
//                            }
//                        } catch(NullPointerException e) {
//                            //e.printStackTrace();
//                        }
//                    }
//                    chatReference.child(id).addValueEventListener(makeValueEventListener(myMeetingCard_chat));
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        };
//        return mChildEventListener;
//    }
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
                        if (isUnread) {
                            try {
                                tv_unread_noti_count.setVisibility(View.VISIBLE);
                                GlobalApplication.unread_noti_count++;
                                tv_unread_noti_count.setText(Integer.toString(GlobalApplication.unread_noti_count));
                            } catch (NullPointerException e) {
                                //e.printStackTrace();
                            }
                        }
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
    private ValueEventListener makeValueEventListener(final MyMeetingCard_Chat myMeetingCard_chat) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    try {
                        ChatData chatData = dataSnapshot.getValue(ChatData.class);
                        if(chatData.message.equals(ChattingActivity.MANAGER_PLACE_TAG)) {
                            myMeetingCard_chat.chat_content.setText("추천 장소를 확인해보세요!");
                        }
                        else {
                            myMeetingCard_chat.chat_content.setText(chatData.message);
                        }
                        SimpleDateFormat format = new SimpleDateFormat("a h:mm");
                        String time = format.format(chatData.time);
                        myMeetingCard_chat.time.setText(time);
                    } catch(NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        return valueEventListener;
    }
    private void createPastMeetingCard(final JSONObject json_data) {
        try {
            LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.past_meeting_root);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            final View mmca = inflater.inflate(R.layout.my_meeting_card_after, mRootLinear, false);
            mRootLinear.addView(mmca);
            final LinearLayout first_pane = mmca.findViewById(R.id.first_pane);
            final RelativeLayout second_pane = mmca.findViewById(R.id.second_pane);
            final FrameLayout third_pane = mmca.findViewById(R.id.third_pane);
            final LinearLayout forth_pane = mmca.findViewById(R.id.forth_pane);
            first_pane.setVisibility(View.VISIBLE);
            second_pane.setVisibility(View.GONE);
            third_pane.setVisibility(View.GONE);
            forth_pane.setVisibility(View.GONE);

            //first pane
            TextView tv_how_were = mmca.findViewById(R.id.tv_how_were);
            final Button btn_go_review = mmca.findViewById(R.id.btn_go_review);

            //second pane
            final Button btn_stars_visual[] = new Button[5];
            final Button btn_stars_fun[] = new Button[5];
            final Button btn_stars_manner[] = new Button[5];
            btn_stars_visual[0] = mmca.findViewById(R.id.btn_star_1);
            btn_stars_visual[1] = mmca.findViewById(R.id.btn_star_2);
            btn_stars_visual[2] = mmca.findViewById(R.id.btn_star_3);
            btn_stars_visual[3] = mmca.findViewById(R.id.btn_star_4);
            btn_stars_visual[4] = mmca.findViewById(R.id.btn_star_5);
            btn_stars_fun[0] = mmca.findViewById(R.id.btn_star_6);
            btn_stars_fun[1] = mmca.findViewById(R.id.btn_star_7);
            btn_stars_fun[2] = mmca.findViewById(R.id.btn_star_8);
            btn_stars_fun[3] = mmca.findViewById(R.id.btn_star_9);
            btn_stars_fun[4] = mmca.findViewById(R.id.btn_star_10);
            btn_stars_manner[0] = mmca.findViewById(R.id.btn_star_11);
            btn_stars_manner[1] = mmca.findViewById(R.id.btn_star_12);
            btn_stars_manner[2] = mmca.findViewById(R.id.btn_star_13);
            btn_stars_manner[3] = mmca.findViewById(R.id.btn_star_14);
            btn_stars_manner[4] = mmca.findViewById(R.id.btn_star_15);


            //third pane
            final EditText et_feedback = mmca.findViewById(R.id.et_feedback);
            Button btn_send_feedback = mmca.findViewById(R.id.btn_send_feedback);
            TextView tv_skip_feedback = mmca.findViewById(R.id.tv_skip_feedback);
            tv_skip_feedback.setPaintFlags(tv_skip_feedback.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(json_data.getString("date"));
                tv_how_were.setText((date.getMonth()+1)+"월 "+ date.getDate() + "일 " + "미팅 어떠셨나요?!");
            } catch(ParseException e) {
                e.printStackTrace();
            }

            btn_go_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                    AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                    fadeIn.setDuration(500);
                    fadeOut.setDuration(500);
                    fadeIn.setFillAfter(true);
                    fadeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            first_pane.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //second_pane.setVisibility(View.VISIBLE);
                            third_pane.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    first_pane.startAnimation(fadeOut);
                    //second_pane.startAnimation(fadeIn);
                    third_pane.startAnimation(fadeIn);
                }
            });
            for (int i = 0; i < 5; i++) {
                final int position = i;
                final Button button_visual = btn_stars_visual[i];
                final Button button_fun = btn_stars_fun[i];
                final Button button_manner = btn_stars_manner[i];
                button_visual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (button_visual.getText().toString().equals("★")) {
                            boolean flag = false;
                            for (int j = position + 1; j < 5; j++) {
                                if (btn_stars_visual[j].getText().toString().equals("★")) {
                                    btn_stars_visual[j].setText("☆");
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                for (int j = 0; j <= position; j++) {
                                    btn_stars_visual[j].setText("☆");
                                }
                            }
                        } else {
                            boolean flag1 = false;
                            boolean flag2 = false;
                            int rate_visual = 0, rate_fun = 0, rate_manner = 0;
                            for (int j = 0; j <= position; j++) {
                                btn_stars_visual[j].setText("★");
                                rate_visual = j+1;
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_fun[k].getText().toString().equals("★")) {
                                    flag1 = true;
                                    rate_fun = k+1;
                                }
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_manner[k].getText().toString().equals("★")) {
                                    flag2 = true;
                                    rate_manner = k+1;
                                }
                            }
                            if(flag1 && flag2) {
                                AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                                AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                                fadeIn.setDuration(500);
                                fadeOut.setDuration(500);
                                fadeIn.setFillAfter(true);
                                final int visual = rate_visual;
                                final int fun = rate_fun;
                                final int manner = rate_manner;
                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        second_pane.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        third_pane.setVisibility(View.VISIBLE);

                                        String url = "http://106.10.39.154:9999/api/meetings/rate/";
                                        ContentValues values = new ContentValues();
                                        try {
                                            //values.put("meeting_id", json_data.getJSONObject("matched_meeting").getInt("id"));
                                            values.put("meeting_id", json_data.getInt("id"));
                                            NetworkTask3 networkTask3 = new NetworkTask3(url, values);
                                            networkTask3.execute();
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                third_pane.startAnimation(fadeIn);
                                second_pane.startAnimation(fadeOut);
                            }
                        }
                    }
                });
                button_fun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (button_fun.getText().toString().equals("★")) {
                            boolean flag = false;
                            for (int j = position + 1; j < 5; j++) {
                                if (btn_stars_fun[j].getText().toString().equals("★")) {
                                    btn_stars_fun[j].setText("☆");
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                for (int j = 0; j <= position; j++) {
                                    btn_stars_fun[j].setText("☆");
                                }
                            }
                        } else {
                            boolean flag1 = false;
                            boolean flag2 = false;
                            int rate_visual = 0, rate_fun = 0, rate_manner = 0;
                            for (int j = 0; j <= position; j++) {
                                btn_stars_fun[j].setText("★");
                                rate_fun = j+1;
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_visual[k].getText().toString().equals("★")) {
                                    flag1 = true;
                                    rate_visual = k+1;
                                }
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_manner[k].getText().toString().equals("★")) {
                                    flag2 = true;
                                    rate_manner = k+1;
                                }
                            }
                            if(flag1 && flag2) {
                                AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                                AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                                fadeIn.setDuration(500);
                                fadeOut.setDuration(500);
                                fadeIn.setFillAfter(true);
                                final int visual = rate_visual;
                                final int fun = rate_fun;
                                final int manner = rate_manner;
                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        second_pane.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        third_pane.setVisibility(View.VISIBLE);
                                        String url = "http://106.10.39.154:9999/api/meetings/rate/";
                                        ContentValues values = new ContentValues();
                                        try {
                                            //values.put("meeting_id", json_data.getJSONObject("matched_meeting").getInt("id"));
                                            values.put("meeting_id", json_data.getInt("id"));
                                            values.put("visual", visual);
                                            values.put("fun", fun);
                                            values.put("manner", manner);
                                            NetworkTask3 networkTask3 = new NetworkTask3(url, values);
                                            networkTask3.execute();
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                third_pane.startAnimation(fadeIn);
                                second_pane.startAnimation(fadeOut);
                            }
                        }
                    }
                });
                button_manner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (button_manner.getText().toString().equals("★")) {
                            boolean flag = false;
                            for (int j = position + 1; j < 5; j++) {
                                if (btn_stars_manner[j].getText().toString().equals("★")) {
                                    btn_stars_manner[j].setText("☆");
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                for (int j = 0; j <= position; j++) {
                                    btn_stars_manner[j].setText("☆");
                                }
                            }
                        } else {
                            boolean flag1 = false;
                            boolean flag2 = false;
                            int rate_visual = 0, rate_fun = 0, rate_manner = 0;
                            for (int j = 0; j <= position; j++) {
                                btn_stars_manner[j].setText("★");
                                rate_manner = j+1;
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_visual[k].getText().toString().equals("★")) {
                                    flag1 = true;
                                    rate_visual = k+1;
                                }
                            }
                            for (int k = 0; k < 5; k++) {
                                if (btn_stars_fun[k].getText().toString().equals("★")) {
                                    flag2 = true;
                                    rate_fun = k+1;
                                }
                            }
                            if(flag1 && flag2) {
                                AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                                AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                                fadeIn.setDuration(500);
                                fadeOut.setDuration(500);
                                fadeIn.setFillAfter(true);
                                final int visual = rate_visual;
                                final int fun = rate_fun;
                                final int manner = rate_manner;
                                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        second_pane.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        third_pane.setVisibility(View.VISIBLE);
                                        String url = "http://106.10.39.154:9999/api/meetings/rate/";
                                        ContentValues values = new ContentValues();
                                        try {
                                            //values.put("meeting_id", json_data.getJSONObject("matched_meeting").getInt("id"));
                                            values.put("meeting_id", json_data.getInt("id"));
                                            values.put("visual", visual);
                                            values.put("fun", fun);
                                            values.put("manner", manner);
                                            NetworkTask3 networkTask3 = new NetworkTask3(url, values);
                                            networkTask3.execute();
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                third_pane.startAnimation(fadeIn);
                                second_pane.startAnimation(fadeOut);
                            }
                        }
                    }
                });
                btn_send_feedback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeIn.setDuration(500);
                        fadeOut.setDuration(500);
                        fadeIn.setFillAfter(true);
                        fadeIn.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                third_pane.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                forth_pane.setVisibility(View.VISIBLE);
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                                String url = "http://106.10.39.154:9999/api/meetings/feedback/";
                                ContentValues values = new ContentValues();
                                try {
                                    //values.put("meeting_id", json_data.getJSONObject("matched_meeting").getInt("id"));
                                    values.put("meeting_id", json_data.getInt("id"));
                                    values.put("feedback", et_feedback.getText().toString());
                                    NetworkTask3 networkTask3 = new NetworkTask3(url, values);
                                    networkTask3.execute();
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        forth_pane.startAnimation(fadeIn);
                        third_pane.startAnimation(fadeOut);

                        Runnable mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                mmca.animate()
                                        .translationY(-1000)
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                mmca.setVisibility(View.GONE);
                                            }});
                            }
                        };

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 2000);
                    }
                });
            }
        } catch(JSONException e) {
            e.printStackTrace();
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

            result = requestHttpURLConnection.request(context, url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            myMeetingCardFrame.setActive_length(jsonArray.length());
            if(myMeetingCardFrame.getActive_length() == 0) {
                btn_go_yamigu.setVisibility(View.VISIBLE);
            }
            else {
                btn_go_yamigu.setVisibility(View.GONE);
            }
            ArrayList<String> date_list = new ArrayList<>();
            for(int i = 0; i < myMeetingCardFrame.getActive_length(); i++) {
                try {
                    final int meeting_id = jsonArray.getJSONObject(i).getInt("id");
                    int matching_id = 0;
                    int partner_uid = 0;
                    int manager_uid = 0;
                    long accepted_at = 0;
                    String manager_name = "";
                    JSONObject received_request = jsonArray.getJSONObject(i).getJSONObject("received_request");
                    JSONObject sent_request = jsonArray.getJSONObject(i).getJSONObject("sent_request");
                    myMeetingCardFrame.mmc_list[i].setId(meeting_id);
                    myMeetingCardFrame.mmc_list[i].setPlace(jsonArray.getJSONObject(i).getInt("place_type"));
                    myMeetingCardFrame.mmc_list[i].setType(jsonArray.getJSONObject(i).getInt("meeting_type"));
                    myMeetingCardFrame.mmc_list[i].setNum_of_applying(received_request.getInt("count"));
                    myMeetingCardFrame.mmc_list[i].setAppeal(jsonArray.getJSONObject(i).getString("appeal"));
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");


                    Date translated_date;
                    Date today = new Date();
                    Calendar cal_meeting_day = Calendar.getInstance();
                    Calendar cal_today = Calendar.getInstance();
                    try {
                        String date_string = jsonArray.getJSONObject(i).getString("date");
                        date_list.add(date_string);
                        translated_date = transFormat.parse(date_string);
                        myMeetingCardFrame.mmc_list[i].setDateString(date_string);
                        cal_meeting_day.setTime(translated_date);
                        cal_today.setTime(today);
                        long l_mday = cal_meeting_day.getTimeInMillis() / (24 * 60 * 60 * 1000);
                        long l_tday = cal_today.getTimeInMillis() / (24 * 60 * 60 * 1000);

                        myMeetingCardFrame.mmc_list[i].setDate(date_string);
                        if(jsonArray.getJSONObject(i).getBoolean("is_matched")) {
                            String place_array[] = {"신촌/홍대", "건대/왕십리", "강남"};
                            String type_array[] = {"2:2", "3:3", "4:4"};
                            final JSONObject matched_meeting = jsonArray.getJSONObject(i).getJSONObject("matched_meeting");
                            partner_uid = matched_meeting.getInt("openby_uid");
                            final int age = matched_meeting.getInt("openby_age");
                            final String partner_belong = matched_meeting.getString("openby_belong");
                            final String partner_department = matched_meeting.getString("openby_department");
                            final String partner_nickname = matched_meeting.getString("openby_nickname");
                            final String place = place_array[jsonArray.getJSONObject(i).getInt("place_type") - 1];
                            final String type = type_array[jsonArray.getJSONObject(i).getInt("meeting_type") - 1];
                            String before_date = jsonArray.getJSONObject(i).getString("date");
                            Date date_obj = new SimpleDateFormat("yyyy-MM-dd").parse(before_date);
                            final String date = date_obj.getMonth() + 1 + "월" + " " + date_obj.getDate() + "일";
                            boolean flag = false;
                            String manager_profile_url = "";
                            for(int j = 0 ; j < received_request.getInt("count"); j++) {
                                JSONObject request_obj = received_request.getJSONArray("data").getJSONObject(j);
                                if(request_obj.getBoolean("is_selected") && request_obj.getInt("receiver") == meeting_id) {
                                    matching_id = request_obj.getInt("id");
                                    manager_uid = request_obj.getInt("manager_uid");
                                    manager_name = request_obj.getString("manager_name");
                                    accepted_at = request_obj.getLong("accepted_at");
                                    manager_profile_url = request_obj.getString("manager_profile");
                                    flag = true;
                                    break;
                                }
                            }
                            if(!flag) {
                                for (int j = 0; j < sent_request.getInt("count"); j++) {
                                    JSONObject request_obj = sent_request.getJSONArray("data").getJSONObject(j);
                                    if(request_obj.getBoolean("is_selected") && request_obj.getInt("sender") == meeting_id) {
                                        matching_id = request_obj.getInt("id");
                                        manager_uid = request_obj.getInt("manager_uid");
                                        manager_name = request_obj.getString("manager_name");
                                        accepted_at = request_obj.getLong("accepted_at");
                                        manager_profile_url = request_obj.getString("manager_profile");
                                        break;
                                    }
                                }
                            }

                            managerDB = FirebaseDatabase.getInstance().getReference().child("user").child(""+manager_uid);

//                            if(!manager_profile_url.isEmpty()) {
//                                ContentValues values = new ContentValues();
//                                NetworkTask4 networkTask4 = new NetworkTask4(manager_profile_url, values, ""+manager_uid);
//                                networkTask4.execute();
//                            }
                            final int matching_id_final = matching_id;
                            final int partner_uid_final = partner_uid;
                            final int manager_uid_final = manager_uid;
                            final String manager_name_final = manager_name;
                            final long accepted_at_final = accepted_at;
                            SimpleDateFormat format = new SimpleDateFormat( "a h:mm");
                            myMeetingCardFrame.mmc_c_list[i].setVisibility(View.VISIBLE);

                            if(!GlobalApplication.unread_chat_map.containsKey(matching_id)) {
                                myMeetingCardFrame.mmc_c_list[i].chat_content.setText("매칭이 완료되었습니다. 채팅을 시작해보세요!");
                                myMeetingCardFrame.mmc_c_list[i].unread_count.setText("0");
                                myMeetingCardFrame.mmc_c_list[i].unread_count.setVisibility(View.INVISIBLE);
                                myMeetingCardFrame.mmc_c_list[i].time.setText(format.format(accepted_at));
                            }
//                            if(!GlobalApplication.matching_id_set.contains(matching_id)) {
//                                final ChildEventListener mChildEventListener = makeChildEventListener(myMeetingCardFrame.mmc_c_list[i], matching_id);
//                                final DatabaseReference mdatabaseReference = loadMessages(matching_id, mChildEventListener);
//                            }
                            GlobalApplication.updateChatPreview(matching_id, myMeetingCardFrame.mmc_c_list[i]);

                            View.OnClickListener goChattingListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    Intent intent = new Intent(getContext(), ChattingActivity.class);
                                    //((MyMeetingCard_Chat)view).unread_count.setText("0");
                                    intent.putExtra("meeting_id", ""+meeting_id);
                                    intent.putExtra("matching_id", ""+matching_id_final);
                                    intent.putExtra("partner_uid", ""+partner_uid_final);
                                    intent.putExtra("manager_uid", ""+manager_uid_final);
                                    intent.putExtra("manager_name", manager_name_final);
                                    intent.putExtra("accepted_at", accepted_at_final);
                                    intent.putExtra("partner_age", age);
                                    intent.putExtra("partner_belong", partner_belong);
                                    intent.putExtra("partner_department", partner_department);
                                    intent.putExtra("partner_nickname", partner_nickname);
                                    intent.putExtra("place", place);
                                    intent.putExtra("date", date);
                                    intent.putExtra("type", type);
//                                    mdatabaseReference.removeEventListener(mChildEventListener);
                                    if(GlobalApplication.unread_chat_map.containsKey(matching_id_final)) {
                                        GlobalApplication.ChatPreviewData cpd = GlobalApplication.unread_chat_map.get(matching_id_final);
                                        cpd.unread_count = 0;
                                        cpd.myMeetingCard_chat.unread_count.setVisibility(View.INVISIBLE);
                                        GlobalApplication.unread_chat_map.put(matching_id_final, cpd);
                                    }

                                    startActivity(intent);
                                    ((MainActivity)context).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                                }
                            };
                            myMeetingCardFrame.mmc_c_list[i].setOnClickListener(goChattingListener);
                            myMeetingCardFrame.mmc_list[i].setOnClickListener(goChattingListener);
                            myMeetingCardFrame.mmc_list[i].setProfile1(matched_meeting.getString("openby_nickname"), matched_meeting.getInt("openby_age"));
                            myMeetingCardFrame.mmc_list[i].setProfile2(matched_meeting.getString("openby_belong"), matched_meeting.getString("openby_department"));
                            myMeetingCardFrame.mmc_list[i].setMatched(true);
                        }
                        else {
                            myMeetingCardFrame.mmc_list[i].setMatched(false);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                myMeetingCardFrame.mmc_list[i].setVisibility(View.VISIBLE);
                ((MainActivity)context).setMyMeetingCount(i+1);
            }
            GlobalApplication.active_date_list = date_list;
            if(MainActivity.dialog.isShowing()) {
                MainActivity.dialog.dismiss();
            }
            ll_root_pane.setVisibility(View.VISIBLE);
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

            result = requestHttpURLConnection.request(context, url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                if(jsonArray.length() > 0) {
                    for(int i = 0; i < jsonArray.length(); i++) {
                        createPastMeetingCard(jsonArray.getJSONObject(i));
                    }
                }
                else {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(MainActivity.dialog.isShowing()) {
                MainActivity.dialog.dismiss();
            }
        }
    }
    public class NetworkTask3 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask3(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(context, url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(MainActivity.dialog.isShowing()) {
                MainActivity.dialog.dismiss();
            }
        }
    }
    public class NetworkTask4 extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        private String manager_uid;
        public NetworkTask4(String url, ContentValues values, String manager_uid) {
            this.url = url;
            this.values = values;
            this.manager_uid = manager_uid;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlO = new URL(url);

                URLConnection conn = urlO.openConnection();
                conn.connect();
                InputStream urlInputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(urlInputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            final String imageBase64 = ImageUtils.encodeBase64(bm);
            managerDB.child("avata").setValue(imageBase64)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                return;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Update Avatar", "failed");
                        }
                    });
            if(MainActivity.dialog.isShowing()) {
                MainActivity.dialog.dismiss();
            }
        }
    }
    public class NetworkTask5 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask5(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }
        @Override
        protected String doInBackground(Void... params) {


            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(context, url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            try {
                fragmentAdapter.clear();
                jsonArray = new JSONArray(s);
                total_num = jsonArray.length();
                Log.d("total_Num", ""+total_num);
                for(int i = 0; i < jsonArray.length(); i++) {
                    MeetingCardFragment meetingCardFragment = new MeetingCardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("TAG", "recommendation");
                    bundle.putInt("type", jsonArray.getJSONObject(i).getInt("meeting_type"));
                    bundle.putString("nickname", jsonArray.getJSONObject(i).getString("openby_nickname"));
                    bundle.putInt("age", jsonArray.getJSONObject(i).getInt("openby_age"));
                    bundle.putString("belong", jsonArray.getJSONObject(i).getString("openby_belong"));
                    bundle.putString("department", jsonArray.getJSONObject(i).getString("openby_department"));
                    bundle.putString("date", jsonArray.getJSONObject(i).getString("date"));
                    bundle.putString("appeal", jsonArray.getJSONObject(i).getString("appeal"));
                    bundle.putString("place_type_name", jsonArray.getJSONObject(i).getString("place_type_name"));
                    bundle.putString("profile_img_url", jsonArray.getJSONObject(i).getString("openby_profile"));
                    meetingCardFragment.setArguments(bundle);
                    fragmentAdapter.addItem(meetingCardFragment);
                }
                refresh();
            } catch(JSONException e) {
                e.printStackTrace();
            }
            MainActivity.dialog.dismiss();
        }
    }
}
