package com.yamigu.yamigu_app.Etc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yamigu.yamigu_app.Activity.ChattingActivity;
import com.yamigu.yamigu_app.Activity.GlobalApplication;
import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.Activity.NotificationActivity;
import com.yamigu.yamigu_app.Activity.SplashActivity;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    private SharedPreferences.Editor editor;
    private String auth_token;
    private int id = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        id = preferences.getInt("fcm_id", 0);
        if(remoteMessage.getData() == null)
            return;
        //String clickAction = remoteMessage.getNotification().getClickAction();
        JSONObject data = new JSONObject(remoteMessage.getData());
        //sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), clickAction, data);

        sendNotification(data);
    }
    private void sendNotification(JSONObject data) {
        if(!preferences.getBoolean("push_noti_avail", true))
            return;
        String title = "";
        String message = "";
        String clickAction = "";
        JSONObject intent_args = new JSONObject();
        try {
            title = data.getString("title");
            message = data.getString("content");
            clickAction = data.getString("clickAction");
            intent_args = new JSONObject(data.getString("intentArgs"));
        } catch(JSONException e) {
            e.printStackTrace();
        }
        if(title == null)
            title = "yamigu notification";
        String activityName;

        activityName = clickAction;


//        //Log.d("ACTIVITYNAME", activityName);
//        Log.d("Content", message);
        Log.d("data", intent_args.toString());

        Intent intent;

        if(activityName.equals(".ChattingActivity")) {
            if(!preferences.getBoolean("chat_noti_avail", true))
                return;
            try {
                if (GlobalApplication.isAppOnForeground(getApplicationContext()) && ((GlobalApplication) getApplicationContext()).getCurrentActivity().getLocalClassName().equals("Activity.ChattingActivity")) {
                    return;
                }
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
            intent = new Intent(this, ChattingActivity.class);
            try {
                intent.putExtra("partner_age", intent_args.getInt("partner_age"));
                intent.putExtra("partner_belong", intent_args.getString("partner_belong"));
                intent.putExtra("partner_department", intent_args.getString("partner_department"));
                intent.putExtra("partner_nickname",  intent_args.getString("partner_nickname"));
                intent.putExtra("date", intent_args.getString("date"));
                intent.putExtra("place", intent_args.getString("place"));
                intent.putExtra("type", intent_args.getString("type"));
                intent.putExtra("meeting_id", intent_args.getString("meeting_id"));
                intent.putExtra("matching_id", intent_args.getString("matching_id"));
                intent.putExtra("manager_name", intent_args.getString("manager_name"));
                intent.putExtra("partner_uid", intent_args.getString("partner_uid"));
                intent.putExtra("manager_uid", intent_args.getString("manager_uid"));
                intent.putExtra("accepted_at", intent_args.getLong("accepted_at"));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            intent = new Intent(this, NotificationActivity.class);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // 오레오(8.0) 이상일 경우 채널을 반드시 생성해야 한다.
        final String CHANNEL_ID = "yamigu_notification_channel_id";
        NotificationManager mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_NAME = "야미구";
            final String CHANNEL_DESCRIPTION = "Channel for yamigu app";
            final int importance = NotificationManager.IMPORTANCE_HIGH;

            // add in API level 26
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            mChannel.setSound(defaultSoundUri, null);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText(message);
        builder.setContentIntent(pendingIntent);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 아래 설정은 오레오부터 deprecated 되면서 NotificationChannel에서 동일 기능을 하는 메소드를 사용.
            builder.setContentTitle(title);
            builder.setSound(defaultSoundUri);
            builder.setVibrate(new long[]{500, 500});
        }
        //Log.d("NOTIID", ""+id);
        mManager.notify(id, builder.build());
        id++;
        editor.putInt("fcm_id", id);
        editor.apply();
    }
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {
        try {
            auth_token = preferences.getString("auth_token", "");
            String url = "http://106.10.39.154:9999/api/fcm/register_device/";
            ContentValues values = new ContentValues();
            values.put("registration_id", token);
            values.put("type", "android");
            NetworkTask networkTask = new NetworkTask(url, values);
            networkTask.execute();
        } catch(NullPointerException e) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = preferences.edit();
            auth_token = preferences.getString("auth_token", "");
            String url = "http://106.10.39.154:9999/api/fcm/register_device/";
            ContentValues values = new ContentValues();
            values.put("registration_id", token);
            values.put("type", "android");
            NetworkTask networkTask = new NetworkTask(url, values);
            networkTask.execute();
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
        }

    }
}

