package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.net.URL;

public class NICEActivity extends AppCompatActivity {

    private WebView mWebView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String URL_INFO = "http://106.10.39.154:5000/checkplus_main"; //휴대폰본인인증 호출하는 URL 입력;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nice);

        mWebView = (WebView) findViewById(R.id.webView);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        //웹뷰의 설정을 다음과 같이 맞춰주시기 바랍니다.
        mWebView.getSettings().setJavaScriptEnabled(true);	//필수설정(true)
        mWebView.getSettings().setDomStorageEnabled(true);		//필수설정(true)
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);	//필수설정(true)

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);



        /**
         !필수사항!

         웹뷰 내 앱링크를 사용하려면 WebViewClient를 반드시 설정하여 주시기바랍니다. (하단 DemoWebViewClient 참고)
         **/
        mWebView.addJavascriptInterface(new MyJavascriptInterface(), "Android");

        mWebView.setWebViewClient(new DemoWebViewClient());

        mWebView.loadUrl(URL_INFO);
    }

    public class DemoWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //웹뷰 내 표준창에서 외부앱(통신사 인증앱)을 호출하려면 intent:// URI를 별도로 처리해줘야 합니다.
            //다음 소스를 적용 해주세요.



            if (url.startsWith("intent://")) {
                Intent intent = null;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        //앱실행
                        startActivity(intent);
                    }
                } catch (URISyntaxException e) {
                    //URI 문법 오류 시 처리 구간

                } catch (ActivityNotFoundException e) {
                    String packageName = intent.getPackage();
                    if (!packageName.equals("")) {
                        // 앱이 설치되어 있지 않을 경우 구글마켓 이동
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    }
                }
                //return  값을 반드시 true로 해야 합니다.
                return true;

            } else if (url.startsWith("https://play.google.com/store/apps/details?id=") || url.startsWith("market://details?id=")) {
                //표준창 내 앱설치하기 버튼 클릭 시 PlayStore 앱으로 연결하기 위한 로직
                Uri uri = Uri.parse(url);
                String packageName = uri.getQueryParameter("id");
                if (packageName != null && !packageName.equals("")) {
                    // 구글마켓 이동
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                }
                //return  값을 반드시 true로 해야 합니다.
                return true;
            }

            //return  값을 반드시 false로 해야 합니다.
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(url.equals("http://106.10.39.154:5000/checkplus_success"))
                view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('pre')[0].innerHTML);"); //<html></html> 사이에 있는 모든 html을 넘겨준다.
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

            String auth_token = "";
            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("checkplus", s);
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
        }
    }
    public class MyJavascriptInterface {

        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            try {
                JSONObject jsonObject = new JSONObject(html);
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
//                intent.putExtra("birthdate", jsonObject.getString("birthdate"));
//                intent.putExtra("gender", jsonObject.getString("gender"));
//                intent.putExtra("phonenumber", jsonObject.getString("mobileno"));
//                intent.putExtra("name", jsonObject.getString("name"));
                editor.putString("birthdate", jsonObject.getString("birthdate"));
                editor.putString("gender_string", jsonObject.getString("gender"));
                editor.putString("phonenumber", jsonObject.getString("mobileno"));
                editor.putString("real_name", jsonObject.getString("name"));
                editor.apply();
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                finish();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
