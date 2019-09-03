package com.yamigu.yamigu_app;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    ImageButton btn_login_kakao;

    private String auth_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        callback = new SessionCallback();
//        Session.getCurrentSession().addCallback(callback);
//        Session.getCurrentSession().checkAndImplicitOpen();
        btn_login_kakao = (ImageButton) findViewById(R.id.btn_login_kakao);

        btn_login_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://192.168.43.223:9999/api/auth/login/";
                ContentValues values = new ContentValues();
                values.put("username", "manager");
                values.put("password", "ghkscjf123");
                //values.put("kakao_account", userProfile.toString());
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        });

    }
    protected void redirectVerificationActivity() {
        final Intent intent = new Intent(this, VerificationActivity.class);
        intent.putExtra("auth_token", auth_token);
        startActivity(intent);
        finish();
    }
    protected void redirectMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("auth_token", auth_token);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
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
            result = requestHttpURLConnection.request(url, values, "POST", ""); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String url = "http://192.168.43.223:9999/api/user/info/";
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ContentValues values = new ContentValues();
            try {
                NetworkTask2 networkTask2 = new NetworkTask2(url, values, jsonObject.getString("key"));
                Log.d("onPostExecute :: ", "jsonObject key: " + jsonObject.getString("key"));
                networkTask2.execute();
                auth_token = jsonObject.getString("key");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        private String token;
        private Boolean isFirstRun;
        public NetworkTask2(String url, ContentValues values, String token) {
            this.url = url;
            this.values = values;
            this.token = token;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(url, values, "GET", token); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            boolean signup_flag = false;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            signup_flag = jsonObject.isNull("gender") || jsonObject.isNull("nickname") || jsonObject.isNull("phone") || jsonObject.isNull("belong") || jsonObject.isNull("department") || jsonObject.isNull("age") || jsonObject.isNull("email");
            isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

            if (!isFirstRun) {
                if(signup_flag) {
                    redirectVerificationActivity();
                }
                else {
                    redirectMainActivity();
                }
            }
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();

        }
    }
}
