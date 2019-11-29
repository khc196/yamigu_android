package com.yamigu.yamigu_app.CustomLayout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class CustomDialog3 extends Dialog {

    public Button btn_no, btn_yes;
    private TextView tv_content;
    private String text, yes_text, no_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog3);
        btn_no = (Button) findViewById(R.id.btn_no);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        tv_content = findViewById(R.id.dialog_text);
        tv_content.setText(text);
        btn_no.setText(no_text);
        btn_yes.setText(yes_text);
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setYesText(String text) {
        yes_text = text;

    }
    public void setNoText(String text) {
        no_text = text;

    }

    public CustomDialog3(Context context) {
        super(context, R.style.ThemeDialogCustom);
    }
}


