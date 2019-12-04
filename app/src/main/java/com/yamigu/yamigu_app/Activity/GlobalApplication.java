package com.yamigu.yamigu_app.Activity;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.yamigu.yamigu_app.CustomLayout.MyMeetingCard_Chat;
import com.yamigu.yamigu_app.Etc.Model.ChatData;
import com.yamigu.yamigu_app.Etc.Model.NotificationData;
import com.yamigu.yamigu_app.Fragment.HomeFragment;
import com.yamigu.yamigu_app.Kakao.KakaoSDKAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    private Activity mCurrentActivity = null;
    public static int unread_noti_count = 0;
    public static int current_chatting_room = 0;
    public static HashMap<String, NotificationData> notification_map;
    public static HashMap<String, Bitmap> bitmap_map;
    public static HashMap<Integer, ChatPreviewData> unread_chat_map;
    public static ArrayList<String> active_date_list;
    public static HashSet<Integer> matching_id_set;
    public static boolean push_noti_avail = true, chat_noti_avail = true;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static  DatabaseReference notiDB;
    private ChildEventListener mChildEventListener;
    public static HomeFragment homefragment;
    public static NotificationActivity notiActivity;
    private static String uid;
    public static class ChatPreviewData {
        public int unread_count;
        public String last_message, last_time;
        public MyMeetingCard_Chat myMeetingCard_chat;
        public DatabaseReference messageDB;
        public ChatPreviewData(int unread_count, String last_message, String last_time, MyMeetingCard_Chat myMeetingCard_chat, DatabaseReference messageDB) {
            this.unread_count = unread_count;
            this.last_message = last_message;
            this.last_time = last_time;
            this.myMeetingCard_chat = myMeetingCard_chat;
            this.messageDB = messageDB;
        }
    }
    public static void updateChatPreview(int matching_id, MyMeetingCard_Chat myMeetingCard_chat) {
        if(!unread_chat_map.containsKey(matching_id)) {
            ChildEventListener mChildEventListener = makeChildEventListener(myMeetingCard_chat, matching_id);
            DatabaseReference messageDB =  FirebaseDatabase.getInstance().getReference("user/" + uid).child("receivedMessages").child(""+matching_id);
            unread_chat_map.put(matching_id, new ChatPreviewData(0, "", "", myMeetingCard_chat, messageDB));
            messageDB.addChildEventListener(mChildEventListener);
        }
        else {
            if(!unread_chat_map.get(matching_id).last_message.isEmpty()){
                myMeetingCard_chat.chat_content.setText(unread_chat_map.get(matching_id).last_message);
            }
            if(!unread_chat_map.get(matching_id).last_time.isEmpty()) {
                myMeetingCard_chat.time.setText(unread_chat_map.get(matching_id).last_time);
            }
            myMeetingCard_chat.unread_count.setText(Integer.toString(unread_chat_map.get(matching_id).unread_count));
            if(unread_chat_map.get(matching_id).unread_count > 0) {
                myMeetingCard_chat.unread_count.setVisibility(View.VISIBLE);
            }
            else {
                myMeetingCard_chat.unread_count.setVisibility(View.INVISIBLE);
            }
        }
    }
    public static GlobalApplication getGlobalApplicationContext() {
        if (instance == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }
        return instance;
    }
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        notification_map = new HashMap<>();
        bitmap_map = new HashMap<>();
        unread_chat_map = new HashMap<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        // Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());
        uid = preferences.getString("uid", "");
        notiDB = FirebaseDatabase.getInstance().getReference("user/"+ uid + "/notifications");
        active_date_list = new ArrayList<>();
        matching_id_set = new HashSet<>();
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null) {
                    try {
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
                        notification_map.put(id, notificationData);
                        if (isUnread) {
                            try {
                                unread_noti_count++;
                                if(homefragment != null && homefragment.isResumed()) {
                                    homefragment.onResume();
                                    homefragment.refresh();
                                }
                                if(notiActivity != null && getCurrentActivity().getLocalClassName().equals("Activity.NotificationActivity")) {
                                    notiActivity.refresh();
                                }
                            } catch (NullPointerException e) {
                                //e.printStackTrace();
                            }
                        }
                    } catch(ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null) {
                    try {
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
                        notification_map.put(id, notificationData);
                        if (isUnread) {
                            try {
                                unread_noti_count++;
                                if(homefragment != null) {
                                    homefragment.onResume();
                                    homefragment.refresh();
                                    Log.d("UNREADCOUNT", unread_noti_count+"");
                                }
                                if(notiActivity != null) {
                                    notiActivity.refresh();
                                }
                            } catch (NullPointerException e) {
                                //e.printStackTrace();
                            }
                        }
                    } catch(ClassCastException e) {
                        e.printStackTrace();
                    }
                }
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
        notiDB.addChildEventListener(mChildEventListener);

    }
    private static ChildEventListener makeChildEventListener(final MyMeetingCard_Chat myMeetingCard_chat, final int matching_id) {
        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("message/" + matching_id);
        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null) {
                    HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                    boolean isUnread = (boolean) mapMessage.get("isUnread");
                    String id = (String) mapMessage.get("id");
                    if(isUnread) {
                        try {
                            int unreadCount = 0;
                            String lastMessage = "";
                            myMeetingCard_chat.unread_count.setVisibility(View.VISIBLE);
                            if(unread_chat_map.containsKey(matching_id)) {
                                ChatPreviewData cpd = unread_chat_map.get(matching_id);
                                cpd.unread_count++;
                                myMeetingCard_chat.unread_count.setText(Integer.toString(cpd.unread_count));
                                GlobalApplication.unread_chat_map.put(matching_id,  cpd);
                            }
                        } catch(NullPointerException e) {
                            //e.printStackTrace();
                        }
                    }
                    chatReference.child(id).addValueEventListener(makeValueEventListener(myMeetingCard_chat));
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
    private static ValueEventListener makeValueEventListener(final MyMeetingCard_Chat myMeetingCard_chat) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    try {
                        int match_id = Integer.parseInt(dataSnapshot.getRef().getParent().getKey());
                        ChatPreviewData cpd = unread_chat_map.get(match_id);
                        ChatData chatData = dataSnapshot.getValue(ChatData.class);
                        if(chatData.message.equals(ChattingActivity.MANAGER_PLACE_TAG)) {
                            myMeetingCard_chat.chat_content.setText("추천 장소를 확인해보세요!");
                            cpd.last_message = "추천 장소를 확인해보세요!";
                        }
                        else {
                            myMeetingCard_chat.chat_content.setText(chatData.message);
                            cpd.last_message = chatData.message;
                        }

                        SimpleDateFormat format = new SimpleDateFormat("a h:mm");
                        String time = format.format(chatData.time);
                        cpd.last_time = time;
                        myMeetingCard_chat.time.setText(time);
                        unread_chat_map.put(match_id, cpd);
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
    @Override
    public void onTerminate() {
        super.onTerminate();
        mCurrentActivity = null;
        instance = null;
    }
    public static void hideKeyboard(final Activity activity) {
        final View view = activity.getCurrentFocus();
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view == null) {
                    View view2 = new View(activity);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                } else {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }, 125);
    }
    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    //return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                    return new AuthType[] {AuthType.KAKAO_ACCOUNT};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }
}
