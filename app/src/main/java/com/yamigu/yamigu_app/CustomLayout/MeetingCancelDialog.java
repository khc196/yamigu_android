package com.yamigu.yamigu_app.CustomLayout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class MeetingCancelDialog extends Dialog {

    public Button btn_no, btn_yes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog_cancel_meeting);
        btn_no = (Button) findViewById(R.id.btn_no);
        btn_yes = (Button) findViewById(R.id.btn_yes);
    }

    public MeetingCancelDialog(Context context) {
        super(context, R.style.ThemeDialogCustom);
    }
}


