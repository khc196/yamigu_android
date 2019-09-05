package com.yamigu.yamigu_app.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

public class CertificationUActivity extends AppCompatActivity {
    private Toolbar tb;
    private ImageButton btn_attach_file;
    private Button btn_go_home;
    private TextView btn_skip;
    private EditText et_university, et_major;
    private String university, major, nickname, friend_code;
    private String auth_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_u);
        tb = (Toolbar) findViewById(R.id.toolbar) ;
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
        Intent intent = getIntent();
        nickname = intent.getExtras().getString("nickname");
        friend_code = intent.getExtras().getString("friend_code");
        auth_token = intent.getExtras().getString("auth_token");

        btn_go_home = (Button) findViewById(R.id.btn_gohome);
        btn_skip = (TextView) findViewById(R.id.btn_skip);
        et_university = (EditText) findViewById(R.id.et_university);
        et_major = (EditText) findViewById(R.id.et_major);

        btn_go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                university = et_university.getText().toString();
                major = et_major.getText().toString();
                String url = "http://147.47.208.44:9999/api/auth/signup/";
                ContentValues values = new ContentValues();
                values.put("real_name", "홍길동");
                values.put("age", 1);
                values.put("phone", "010-0000-0000");
                values.put("gender", 1);
                values.put("nickname", nickname);
                values.put("is_student", true);
                values.put("belong", university);
                values.put("department", major);

                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        });
        btn_skip.setPaintFlags(btn_skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("auth_token", auth_token);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
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

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("auth_token", auth_token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
        }
    }
}
