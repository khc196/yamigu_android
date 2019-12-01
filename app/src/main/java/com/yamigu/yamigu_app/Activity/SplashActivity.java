package com.yamigu.yamigu_app.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.app.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.concurrent.Executor;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;
    private Runnable mRunnable1, mRunnable2, mRunnable3, mRunnable4;
    private ImageView before_logo;
    private LinearLayout after_logo;
    private SessionCallback callback;
    private boolean is_initialized = false;
    private boolean is_loggedin = false;
    private boolean is_signedup = false;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private JSONObject jsonObject;
    private FirebaseAuth mAuth;
    private Boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getHashKey(getApplicationContext());

        before_logo = (ImageView) findViewById(R.id.before_logo);
        after_logo = (LinearLayout) findViewById(R.id.yamigu_logo);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        mAuth = FirebaseAuth.getInstance();
        mHandler = new Handler();
        mRunnable1 = new Runnable() {
            @Override
            public void run() {
                before_logo.animate()
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                after_logo.setVisibility(View.VISIBLE);
                            }});
                after_logo.animate()
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
                if(isFirstRun) {
                    mHandler.postDelayed(mRunnable3, 2000);
                }
                else {
                    final Session session = Session.getCurrentSession();
                    session.addCallback(new SessionCallback());
                    if(!session.checkAndImplicitOpen()) {
                        //session.open(AuthType.KAKAO_ACCOUNT, SplashActivity.this);
                        session.open(AuthType.KAKAO_LOGIN_ALL, SplashActivity.this);
                    }
                }
            }
        };
        mRunnable3 = new Runnable() {
            @Override
            public void run() {
                redirectMainOnboardingActivity();
            }
        };
        mRunnable4 = new Runnable() {
            @Override
            public void run() {
                if(is_signedup) {
                    redirectMainActivity();
                }
                else {
                    redirectVerificationActivity();
                }
            }
        };
        /*
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
            redirectMainActivity();
        } catch(JSONException e) {
            e.printStackTrace();
        }
         */

        mHandler.postDelayed(mRunnable1, 1000);
        mHandler.postDelayed(mRunnable2, 1000);

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
    }

    private void clearReferences(){
        Activity currActivity = ((GlobalApplication)getApplicationContext()).getCurrentActivity();
        if (this.equals(currActivity))
            ((GlobalApplication)getApplicationContext()).setCurrentActivity(null);
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
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Nullable
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;
        try {
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Log.d(TAG, keyHash);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
        if (keyHash != null) {
            return keyHash;
        } else {
            return null;
        }
    }
    protected  void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    protected void redirectVerificationActivity() {
        final Intent intent = new Intent(this, VerificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    redirectLoginActivity();
                }
                // 사용자정보 요청에 성공한 경우,
                @Override
                public void onSuccess(UserProfile userProfile) {
                    String url = "http://106.10.39.154:9999/api/oauth/kakao/";
                    String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
                    ContentValues values = new ContentValues();
                    values.put("access_token", access_token);
                    Log.d("Kakao", access_token);
                    //values.put("kakao_account", userProfile.toString());
                    if(!is_loggedin) {
                        NetworkTask networkTask = new NetworkTask(url, values);
                        is_loggedin = true;
                        networkTask.execute();
                    }
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
            String url = "http://106.10.39.154:9999/api/user/info/";
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            ContentValues values = new ContentValues();
            try {
                auth_token = jsonObject.getString("key");
                NetworkTask2 networkTask2 = new NetworkTask2(url, values, auth_token);
                networkTask2.execute();
                //Log.d("onPostExecute :: ", "jsonObject key: " + auth_token);
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
            String firebase_token = "";
            try {
                jsonObject = new JSONObject(s);
                Log.d("Login info", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                signup_flag = jsonObject.getString("nickname").equals("null");
                firebase_token = jsonObject.getString("firebase_token");
                Log.d("NICKNAME", jsonObject.getString("nickname"));
            } catch(JSONException e) {
                e.printStackTrace();
            }


            is_signedup = !signup_flag;
            if (is_signedup) {
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
                    editor.putInt("num_of_ticket", jsonObject.getInt("ticket"));
                    editor.putString("real_name", jsonObject.getString("real_name"));
                    editor.putString("phonenumber", jsonObject.getString("phone"));

                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("FIREBASE", firebase_token);
            mAuth.signInWithCustomToken(firebase_token)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String TAG = "SPLASH Firebase login";
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCustomToken:success");
                                String url = "http://106.10.39.154:9999/api/fcm/register_device/";
                                String fcm_token = FirebaseInstanceId.getInstance().getToken();
                                ContentValues values = new ContentValues();
                                values.put("registration_id", fcm_token);
                                values.put("type", "android");

                                NetworkTask3 networkTask3 = new NetworkTask3(url, values);
                                networkTask3.execute();
                                FirebaseUser user = mAuth.getCurrentUser();
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

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mHandler.postDelayed(mRunnable4, 300);
        }
    }
}
