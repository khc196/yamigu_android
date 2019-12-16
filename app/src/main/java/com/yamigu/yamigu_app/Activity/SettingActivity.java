package com.yamigu.yamigu_app.Activity;


import android.app.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.yamigu.yamigu_app.CustomLayout.CustomDialog3;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONArray;

import java.util.jar.Attributes;

public class SettingActivity extends AppCompatActivity {
    private Toolbar tb;
    private Switch switch_push, switch_chatting;
    private Button btn_view_notification, btn_app_version, btn_view_private, btn_view_using, btn_logout, btn_withdrawal;
    private TextView tv_version;
    private SharedPreferences preferences, preferences2;
    private SharedPreferences.Editor editor, editor2;
    private CustomDialog3 popupDialog;
    private String auth_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_setting);
        tb = (Toolbar) findViewById(R.id.toolbar);
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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences2 = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        editor = preferences.edit();
        editor2 = preferences2.edit();

        auth_token = preferences.getString("auth_token", "");
        switch_push = (Switch) findViewById(R.id.switch_push);
        switch_chatting = (Switch) findViewById(R.id.switch_chatting);
        btn_view_notification = (Button) findViewById(R.id.btn_view_notification);
        btn_app_version = (Button) findViewById(R.id.btn_app_version);
        btn_view_private = (Button) findViewById(R.id.btn_view_private);
        btn_view_using = (Button) findViewById(R.id.btn_view_using);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_withdrawal = findViewById(R.id.btn_withdrawal);
        tv_version = findViewById(R.id.tv_version);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            tv_version.setText(versionName);
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        GlobalApplication.chat_noti_avail = preferences2.getBoolean("chat_noti_avail", true);
        GlobalApplication.push_noti_avail = preferences2.getBoolean("push_noti_avail", true);

        switch_push.setChecked(GlobalApplication.push_noti_avail);
        switch_chatting.setChecked(GlobalApplication.chat_noti_avail);

        switch_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalApplication.push_noti_avail = !GlobalApplication.push_noti_avail;
                editor2.putBoolean("push_noti_avail", GlobalApplication.push_noti_avail);
                if(!GlobalApplication.push_noti_avail) {
                    GlobalApplication.chat_noti_avail = false;
                    editor2.putBoolean("chat_noti_avail", GlobalApplication.chat_noti_avail);
                    switch_chatting.setChecked(GlobalApplication.chat_noti_avail);
                }
                editor2.apply();

            }
        });
        switch_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalApplication.chat_noti_avail = !GlobalApplication.chat_noti_avail;
                editor2.putBoolean("chat_noti_avail", GlobalApplication.chat_noti_avail);
                editor2.apply();
            }
        });
        btn_withdrawal.setPaintFlags(btn_withdrawal.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_view_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog("로그아웃 하시겠습니까?");
            }
        });
        btn_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog("회원탈퇴시 3개월간 재가입이 불가능합니다.\n정말 탈퇴하시겠습니까?");
            }
        });
        btn_view_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PrivacyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_view_using.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
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
    private void Dialog(final String text) {
        popupDialog = new CustomDialog3(SettingActivity.this);
        popupDialog.setText(text);
        popupDialog.setYesText("예");
        popupDialog.setNoText("아니요");
        popupDialog.setCancelable(true);
        popupDialog.setCanceledOnTouchOutside(true);


        popupDialog.getWindow().setGravity(Gravity.CENTER);
        popupDialog.show();
        popupDialog.btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDialog.dismiss();
            }
        });
        popupDialog.btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(text.equals("로그아웃 하시겠습니까?")) {
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Session session = Session.getCurrentSession();
                            preferences.edit().clear().commit();
                            session.close();
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().remove("isFirstRun").commit();
                            redirectLoginActivity();
                        }
                    });
                }
                else {
                    // TODO 회원 탈퇴 구현
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Session session = Session.getCurrentSession();
                            preferences.edit().clear().commit();
                            session.close();
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().remove("isFirstRun").commit();
                            String url = "http://106.10.39.154:9999/api/auth/withdrawal/";
                            ContentValues values = new ContentValues();
                            NetworkTask networkTask = new NetworkTask(url, values);
                            networkTask.execute();
                        }
                    });
                }
            }
        });
    }
    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
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
            redirectLoginActivity();
        }
    }
}