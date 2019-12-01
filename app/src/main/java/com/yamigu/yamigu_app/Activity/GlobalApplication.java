package com.yamigu.yamigu_app.Activity;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.collection.ArraySet;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.yamigu.yamigu_app.Etc.Model.NotificationData;
import com.yamigu.yamigu_app.Fragment.HomeFragment;
import com.yamigu.yamigu_app.Kakao.KakaoSDKAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    private Activity mCurrentActivity = null;
    public static int unread_noti_count = 0;
    public static int current_chatting_room = 0;
    public static HashMap<String, NotificationData> notification_map;
    public static HashMap<String, Bitmap> bitmap_map;
    public static HashMap<Integer, Integer> unread_chat_map;
    public static ArrayList<String> active_date_list;
    public static boolean push_noti_avail = true, chat_noti_avail = true;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static  DatabaseReference notiDB;
    private ChildEventListener mChildEventListener;
    public static HomeFragment homefragment;
    public static NotificationActivity notiActivity;
    private String uid;
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
    @Override
    public void onTerminate() {
        super.onTerminate();
        mCurrentActivity = null;
        instance = null;
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
