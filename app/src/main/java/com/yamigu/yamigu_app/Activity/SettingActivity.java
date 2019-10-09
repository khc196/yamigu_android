package com.yamigu.yamigu_app.Activity;

import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

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
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
}
