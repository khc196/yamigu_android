package com.yamigu.yamigu_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;
    private Runnable mRunnable1, mRunnable2;
    private ImageView before_logo, logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        before_logo = (ImageView) findViewById(R.id.before_logo);
        logo = (ImageView) findViewById(R.id.logo);

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
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Intent intent = new Intent(getApplicationContext(), MainOnboardingActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable1, 1000);
        mHandler.postDelayed(mRunnable2, 3000);
    }
}
