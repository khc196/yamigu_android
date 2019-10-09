package com.yamigu.yamigu_app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;
    private Runnable mRunnable1, mRunnable2;
    private ImageView before_logo, logo;
    private SessionCallback callback;
    private boolean is_initialized = false;
    private boolean is_loggedin = false;
    private boolean is_signedup = false;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        before_logo = (ImageView) findViewById(R.id.before_logo);
        logo = (ImageView) findViewById(R.id.logo);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        mRunnable1 = new Runnable() {
            @Override
            public void run() {
                before_logo.animate()
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                logo.setVisibility(View.VISIBLE);
                            }});
                logo.animate()
                        .alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                            }});
            }
        };
        mRunnable2 = new Runnable() {
            @Override
            public void run() {
                if(is_loggedin) {
                    if(is_signedup) {
                        try {
                            editor.putString("nickname", jsonObject.getString("nickname"));
                            editor.putString("phone", jsonObject.getString("phone"));
                            editor.putString("belong", jsonObject.getString("belong"));
                            editor.putString("department", jsonObject.getString("department"));
                            editor.putString("profile", jsonObject.getString("image"));
                            editor.putInt("gender", jsonObject.getInt("gender"));
                            editor.putInt("age", jsonObject.getInt("age"));
                            editor.apply();
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                        redirectMainActivity();
                    }
                    else
                        redirectVerificationActivity();
                }
                else {
                    redirectMainOnboardingActivity();
                }

            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable1, 1000);
        Session session = Session.getCurrentSession();
        session.addCallback(new SessionCallback());
        if(!session.checkAndImplicitOpen()) {
            session.open(AuthType.KAKAO_LOGIN_ALL, SplashActivity.this);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    protected void redirectVerificationActivity() {
        final Intent intent = new Intent(this, VerificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    protected void redirectMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    protected void redirectMainOnboardingActivity() {
        Intent intent = new Intent(getApplicationContext(), MainOnboardingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
        finish();
    }
    public class SessionCallback implements ISessionCallback {
        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            requestMe();
        }
        // 로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }
        // 사용자 정보 요청
        public void requestMe() {
            // 사용자정보 요청 결과에 대한 Callback
            UserManagement.getInstance().requestMe(new MeResponseCallback() {
                // 세션 오픈 실패. 세션이 삭제된 경우,
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
                }
                // 회원이 아닌 경우,
                @Override
                public void onNotSignedUp() {
                    Log.e("SessionCallback :: ", "onNotSignedUp");
                }
                // 사용자정보 요청에 성공한 경우,
                @Override
                public void onSuccess(UserProfile userProfile) {
                    String url = "http://147.47.208.44:9999/api/oauth/kakao/";
                    String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
                    ContentValues values = new ContentValues();
                    values.put("access_token", access_token);
                    //values.put("kakao_account", userProfile.toString());
                    is_loggedin = true;
                    NetworkTask networkTask = new NetworkTask(url, values);
                    networkTask.execute();
                }
                // 사용자 정보 요청 실패
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());
                }
            });
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
            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", ""); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String url = "http://147.47.208.44:9999/api/user/info/";
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ContentValues values = new ContentValues();
            try {
                NetworkTask2 networkTask2 = new NetworkTask2(url, values, jsonObject.getString("key"));
                networkTask2.execute();
                Log.d("onPostExecute :: ", "jsonObject key: " + jsonObject.getString("key"));
                auth_token = jsonObject.getString("key");
                editor.putString("auth_token", auth_token);
                editor.apply();
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

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "GET", token); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            jsonObject = null;
            boolean signup_flag = false;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                signup_flag = jsonObject.getString("nickname").isEmpty();
                isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
            } catch(JSONException e) {
                e.printStackTrace();
            }

            isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

            if (!isFirstRun) {
                is_signedup = !signup_flag;
            }
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
            mHandler.postDelayed(mRunnable2, 1000);

        }
    }
}
