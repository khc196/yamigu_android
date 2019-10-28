package com.yamigu.yamigu_app.Activity;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.yamigu.yamigu_app.R;

public class SettingActivity extends AppCompatActivity {
    private Toolbar tb;
    private Switch switch_push, switch_chatting;
    private Button btn_view_notification, btn_app_version, btn_view_private, btn_view_using, btn_logout;
    private TextView btn_withdrawal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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

        switch_push = (Switch) findViewById(R.id.switch_push);
        switch_chatting = (Switch) findViewById(R.id.switch_chatting);
        btn_view_notification = (Button) findViewById(R.id.btn_view_notification);
        btn_app_version = (Button) findViewById(R.id.btn_app_version);
        btn_view_private = (Button) findViewById(R.id.btn_view_private);
        btn_view_using = (Button) findViewById(R.id.btn_view_using);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_withdrawal = (TextView) findViewById(R.id.btn_withdrawal);

        btn_withdrawal.setPaintFlags(btn_withdrawal.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        redirectLoginActivity();
                    }
                });
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
    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}