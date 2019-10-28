package com.yamigu.yamigu_app.CustomLayout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class CustomDialog extends Dialog {

    private TextView mTitleView;
    private TextView mContentView1, mContentView2, mContentView3;
    private String mTitle;
    private String mContent1, mContent2, mContent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog);



        mTitleView = (TextView) findViewById(R.id.dialog_title);
        mContentView1 = (TextView) findViewById(R.id.dialog_text1);
        mContentView2 = (TextView) findViewById(R.id.dialog_text2);
        mContentView3 = (TextView) findViewById(R.id.dialog_text3);

        mTitleView.setText(mTitle);
        mContentView1.setText(mContent1);
        mContentView2.setText(mContent2);
        mContentView3.setText(mContent3);

    }

    public CustomDialog(Context context, String title, String content1, String content2, String content3) {
        super(context, R.style.ThemeDialogCustom);
        this.mTitle = title;
        this.mContent1=content1;
        this.mContent2=content2;
        this.mContent3=content3;
    }


}


