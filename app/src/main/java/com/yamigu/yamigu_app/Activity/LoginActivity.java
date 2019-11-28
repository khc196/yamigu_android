package com.yamigu.yamigu_app.Activity;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    ImageButton btn_login_kakao;
    private LoginButton btn_kakao_login;
    private SessionCallback callback;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private Boolean isFirstRun;
    private Boolean mauth_flag;
    ProgressDialog dialog = null;
    Activity me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mauth_flag = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
//        callback = new SessionCallback();
//        Session.getCurrentSession().addCallback(callback);
//        Session.getCurrentSession().checkAndImplicitOpen();
        mAuth = FirebaseAuth.getInstance();

        btn_login_kakao = (ImageButton) findViewById(R.id.btn_login_kakao);
        me = this;
        btn_login_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(me, "", "로그인 중입니다...", true);
                Session session = Session.getCurrentSession();
                session.addCallback(new SessionCallback());
                if(!session.checkAndImplicitOpen()) {
                    //session.open(AuthType.KAKAO_ACCOUNT, LoginActivity.this);
                    session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
                }
            }
        });
        btn_kakao_login = (LoginButton) findViewById(R.id.btn_kakao_login);

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
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
        clearReferences();
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
    private void clearReferences(){
        Activity currActivity = ((GlobalApplication)getApplicationContext()).getCurrentActivity();
        if (this.equals(currActivity))
            ((GlobalApplication)getApplicationContext()).setCurrentActivity(null);
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
            dialog.dismiss();
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
                    String url = "http://106.10.39.154:9999/api/oauth/kakao/";
                    String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
                    ContentValues values = new ContentValues();
                    values.put("access_token", access_token);
                    //values.put("kakao_account", userProfile.toString());
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

    protected void redirectVerificationActivity() {
        final Intent intent = new Intent(this, VerificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
    protected void redirectMainActivity() {
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", ""); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String url = "http://106.10.39.154:9999/api/user/info/";
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                return;
            }
            ContentValues values = new ContentValues();
            try {
                NetworkTask2 networkTask2 = new NetworkTask2(url, values, jsonObject.getString("key"));
                //Log.d("onPostExecute :: ", "jsonObject key: " + jsonObject.getString("key"));
                networkTask2.execute();
                auth_token = jsonObject.getString("key");
                editor.putString("auth_token", auth_token);
                editor.apply();
            } catch (JSONException e) {
                dialog.dismiss();
                e.printStackTrace();
            }

        }
    }
    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        private String token;

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
            JSONObject jsonObject = null;
            boolean signup_flag = false;
            String firebase_token = "";
            isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                signup_flag = jsonObject.getString("nickname").equals("null");
                firebase_token = jsonObject.getString("firebase_token");
            } catch(JSONException e) {
                e.printStackTrace();
                redirectVerificationActivity();
                return;
            }
            if(signup_flag) {
                redirectVerificationActivity();
                return;
            }
            try {
                editor.putString("nickname", jsonObject.getString("nickname"));
                editor.putString("phone", jsonObject.getString("phone"));
                editor.putString("belong", jsonObject.getString("belong"));
                editor.putString("department", jsonObject.getString("department"));
                editor.putString("profile", jsonObject.getString("image"));
                editor.putInt("gender", jsonObject.getInt("gender"));
                editor.putInt("age", jsonObject.getInt("age"));
                editor.putString("uid", jsonObject.getString("uid"));
                editor.putInt("user_certified", jsonObject.getInt("user_certified"));
                editor.putBoolean("is_student", jsonObject.getBoolean("is_student"));
                editor.apply();
            } catch(JSONException e) {
                e.printStackTrace();
            }
            mAuth.signInWithCustomToken(firebase_token)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String TAG = "SPLASH Firebase login";
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCustomToken:success");
                                if(!mauth_flag) {
                                    mauth_flag = true;
                                    //redirectVerificationActivity();
                                    dialog.dismiss();
                                    redirectMainActivity();
                                }
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                                //Toast.makeText(CustomAuthActivity.this, "Authentication failed.",
                                //       Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });

        }
    }
}
