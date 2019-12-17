package com.yamigu.yamigu_app.CustomLayout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class CustomDialog2 extends Dialog {

    public Button btn_yes;
    public TextView dialog_text;
    public String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog2);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        dialog_text = findViewById(R.id.dialog_text);
        dialog_text.setText(text);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public CustomDialog2(Context context) {
        super(context, R.style.ThemeDialogCustom);
    }
}


