package com.yamigu.yamigu_app;

import android.content.Intent;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {
    private Toolbar tb;
    private RadioButton radio_agree_all, radio_agree_using, radio_agree_private;
    private EditText et_nickname, et_friendcode;
    private Button btn_select_student, btn_select_worker, btn_certify;
    private TextView btn_view_using, btn_view_private;
    private Selector selector;
    private SelectorR selectorR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setNavigationIcon(R.drawable.arrow_back_ios);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

        selector = new Selector();
        selectorR = new SelectorR();
        btn_view_using.setPaintFlags(btn_view_using.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_view_using.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TermsActivity.class);
                startActivity(intent);
            }
        });
        btn_view_private.setPaintFlags(btn_view_private.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_view_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PrivacyActivity.class);
                startActivity(intent);
            }
        });
        btn_certify.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
        btn_select_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleStudent();
                if(selector.isStudent()) {
                    btn_select_student.setTextColor(getResources().getColor(R.color.colorYellow));
                    btn_select_student.setBackgroundResource(R.drawable.state_pressed_yellow);
                    btn_select_worker.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_select_worker.setBackgroundResource(R.drawable.state_pressed_gray);
                    if(selectorR.isAgreed_all()) {
                        btn_certify.setTextColor(getResources().getColor(R.color.colorPoint));
                        btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                    }
                }
                else {
                    btn_select_student.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_select_student.setBackgroundResource(R.drawable.state_pressed_gray);
                    btn_certify.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
                }
            }
        });
        btn_select_worker.setTextColor(getResources().getColor(R.color.colorNonselect));
        btn_select_worker.setBackgroundResource(R.drawable.state_pressed_gray);
        btn_select_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleWorker();
                if(selector.isWorker()) {
                    btn_select_worker.setTextColor(getResources().getColor(R.color.colorYellow));
                    btn_select_worker.setBackgroundResource(R.drawable.state_pressed_yellow);
                    btn_select_student.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_select_student.setBackgroundResource(R.drawable.state_pressed_gray);
                    if(selectorR.isAgreed_all()) {
                        btn_certify.setTextColor(getResources().getColor(R.color.colorPoint));
                        btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                    }
                }
                else {
                    btn_select_worker.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_select_worker.setBackgroundResource(R.drawable.state_pressed_gray);
                    btn_certify.setTextColor(getResources().getColor(R.color.colorNonselect));
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
                startActivity(intent);
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
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                }
                else {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorNonselect));
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
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                }
                else {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorNonselect));
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
                    btn_certify.setTextColor(getResources().getColor(R.color.colorPoint));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_orange);
                }
                else {
                    btn_certify.setTextColor(getResources().getColor(R.color.colorNonselect));
                    btn_certify.setBackgroundResource(R.drawable.state_pressed_gray);
                }
            }
        });
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
}
