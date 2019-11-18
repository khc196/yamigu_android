package com.yamigu.yamigu_app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.yamigu.yamigu_app.R;

public class VerificationActivity extends AppCompatActivity {
    private Toolbar tb;
    private Button btn_verify_phone;
    private String auth_token;
    private SharedPreferences preferences;
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
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

        auth_token = preferences.getString("auth_token", "");
        btn_verify_phone = (Button) findViewById(R.id.btn_verify_phone);
        btn_verify_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NICEActivity.class);
                //Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);

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
}
