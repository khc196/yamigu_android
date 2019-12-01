package com.yamigu.yamigu_app.Activity;

import android.app.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private Toolbar tb;
    private RadioButton radio_agree_all, radio_agree_using, radio_agree_private;
    private EditText et_nickname, et_friendcode;
    private Button btn_select_student, btn_select_worker, btn_certify;
    private TextView btn_view_using, btn_view_private, tv_available_nickname;
    private Selector selector;
    private SelectorR selectorR;
    private boolean nickname_validated;
    private String nickname;
    private String friend_code;
    private String auth_token;
    private SharedPreferences preferences;

    private boolean validated_from_server;
    private String real_name, birthdate, gender, phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.overall_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch(NullPointerException e) {

                }
                return true;
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);


        tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setNavigationIcon(R.drawable.arrow_back_ios);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

//        real_name = intent.getExtras().getString("name");
//        phonenumber = intent.getExtras().getString("phonenumber");
//        gender = intent.getExtras().getString("gender");
//        birthdate = intent.getExtras().getString("birthdate");
        auth_token = preferences.getString("auth_token", "");

        radio_agree_all = (RadioButton) findViewById(R.id.radio_agree_all);
        radio_agree_using = (RadioButton) findViewById(R.id.radio_agree_using);
        radio_agree_private = (RadioButton) findViewById(R.id.radio_agree_private);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_friendcode = (EditText) findViewById(R.id.et_friendcode);
        btn_select_student = (Button) findViewById(R.id.btn_select_student);
        btn_select_worker = (Button) findViewById(R.id.btn_select_worker);
        btn_certify = (Button) findViewById(R.id.btn_certify);
        btn_view_using = (TextView) findViewById(R.id.btn_view_using);
        btn_view_private = (TextView) findViewById(R.id.btn_view_private);
        tv_available_nickname = (TextView) findViewById(R.id.tv_available_nickname);

        selector = new Selector();
        selectorR = new SelectorR();
        btn_view_using.setPaintFlags(btn_view_using.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_view_using.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TermsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_view_private.setPaintFlags(btn_view_private.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_view_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PrivacyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
        btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
        btn_select_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleStudent();
                if(selector.isStudent()) {
                    btn_select_student.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_select_student.setBackgroundResource(R.drawable.state_pressed_yellow);
                    btn_select_worker.setTextColor(getResources().getColor(R.color.colorHint));
                    btn_select_worker.setBackgroundResource(R.drawable.state_pressed_gray);
                    if(selectorR.isAgreed_all()) {
                        btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                        btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                    }
                }
                else {
                    btn_select_student.setTextColor(getResources().getColor(R.color.colorHint));
                    btn_select_student.setBackgroundResource(R.drawable.state_pressed_gray);
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
                }
            }
        });
        btn_select_worker.setTextColor(getResources().getColor(R.color.colorHint));
        btn_select_worker.setBackgroundResource(R.drawable.state_pressed_gray);
        btn_select_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleWorker();
                if(selector.isWorker()) {
                    btn_select_worker.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_select_worker.setBackgroundResource(R.drawable.state_pressed_yellow);
                    btn_select_student.setTextColor(getResources().getColor(R.color.colorHint));
                    btn_select_student.setBackgroundResource(R.drawable.state_pressed_gray);
                    if(selectorR.isAgreed_all()) {
                        btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                        btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                    }
                }
                else {
                    btn_select_worker.setTextColor(getResources().getColor(R.color.colorHint));
                    btn_select_worker.setBackgroundResource(R.drawable.state_pressed_gray);
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
                }
            }
        });
        btn_certify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(selector.isStudent()) {
                    intent = new Intent(getApplicationContext(), CertificationUActivity.class);
                }
                else if(selector.isWorker()) {
                    intent = new Intent(getApplicationContext(), CertificationWActivity.class);
                }
                else {
                    return;
                }
                if(!selectorR.isAgreed_all()) {
                    return;
                }
//                intent.putExtra("nickname", nickname);
//                intent.putExtra("friend_code", friend_code);
//                intent.putExtra("realname", real_name);
//                intent.putExtra("phonenumber", phonenumber);
//                intent.putExtra("gender", gender);
//                intent.putExtra("birthdate", birthdate);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        radio_agree_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectorR.isAgreed_all()) {
                    selectorR.turnOffAgreed_term();
                    selectorR.turnOffAgreed_privacy();
                }
                else {
                    selectorR.turnOnAgreed_term();
                    selectorR.turnOnAgreed_privacy();
                }
                radio_agree_using.setChecked(selectorR.isAgreed_term());
                radio_agree_private.setChecked(selectorR.isAgreed_privacy());
                radio_agree_all.setChecked(selectorR.isAgreed_all());
                if(selectorR.isAgreed_all() && (selector.isStudent() || selector.isWorker())) {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                }
                else {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
                }
            }
        });
        radio_agree_using.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectorR.isAgreed_term()) {
                    selectorR.turnOnAgreed_term();
                }
                else {
                    selectorR.turnOffAgreed_term();
                }
                radio_agree_using.setChecked(selectorR.isAgreed_term());
                radio_agree_all.setChecked(selectorR.isAgreed_all());
                if(selectorR.isAgreed_all() && (selector.isStudent() || selector.isWorker())) {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                }
                else {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
                }
            }
        });
        radio_agree_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectorR.isAgreed_privacy()) {
                    selectorR.turnOnAgreed_privacy();
                }
                else {
                    selectorR.turnOffAgreed_privacy();
                }
                radio_agree_private.setChecked(selectorR.isAgreed_privacy());
                radio_agree_all.setChecked(selectorR.isAgreed_all());
                if(selectorR.isAgreed_all() && (selector.isStudent() || selector.isWorker())) {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                }
                else {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
                }
            }
        });
        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals("")) {
                    String url = "http://106.10.39.154:9999/api/user/validation/nickname/" + editable.toString();
                    ContentValues values = new ContentValues();
                    NetworkTask networkTask = new NetworkTask(url, values);
                    networkTask.execute();
                }
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
    private class Selector {
        private boolean student, worker;
        public Selector() {
            student = false;
            worker = false;
        }

        public boolean isStudent() {
            return student;
        }
        public boolean isWorker() {
            return worker;
        }
        public void toggleStudent() {
            student = !student;
            if(student) worker = false;
        }
        public void toggleWorker() {
            worker = !worker;
            if(worker) student = false;
        }
    }
    private class SelectorR {
        private boolean agreed_term, agreed_privacy;
        public SelectorR() {
            agreed_term = false;
            agreed_privacy = false;
        }
        public boolean isAgreed_all() { return agreed_term && agreed_privacy; }
        public boolean isAgreed_term() {
            return agreed_term;
        }
        public boolean isAgreed_privacy() {
            return agreed_privacy;
        }
        public void turnOnAgreed_term() {
            agreed_term = true;
        }
        public void turnOnAgreed_privacy() {
            agreed_privacy = true;
        }
        public void turnOffAgreed_term() {
            agreed_term = false;
        }
        public void turnOffAgreed_privacy() {
            agreed_privacy = false;
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

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            validated_from_server = false;

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                validated_from_server = jsonObject.getBoolean("is_available");
            } catch(JSONException e) {
                validated_from_server = false;
            } catch(NullPointerException e) {
                validated_from_server = false;
            }

            String pattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$";
            Editable editable = et_nickname.getText();
            boolean is_validate = editable.length() <= 6 && Pattern.matches(pattern, editable.toString());
            if(!editable.toString().isEmpty()) {
                tv_available_nickname.setVisibility(View.VISIBLE);
                if (!is_validate || !validated_from_server) {
                    tv_available_nickname.setTextColor(getResources().getColor(R.color.colorRed));
                    tv_available_nickname.setText("사용 불가능합니다.");
                    nickname_validated = false;
                    nickname = "";
                } else {
                    tv_available_nickname.setTextColor(getResources().getColor(R.color.colorBlue));
                    tv_available_nickname.setText("사용 가능합니다.");
                    nickname_validated = true;
                    nickname = editable.toString();
                }
            }
            else {
                tv_available_nickname.setVisibility(View.GONE);
            }
        }
    }
}
